package edu.escuelaing.arem.ASE.app;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;

/**
 * Esta clase implementa un servidor web simple que maneja peticiones HTTP.
 * El servidor escucha en el puerto 8080 y puede servir archivos estáticos
 * así como manejar solicitudes REST básicas (GET y POST).
 */
public class SimpleWebServer {

    private static final int PORT = 8080;
    private static String WEB_ROOT;
    private static final Map<String, RESTService> getServices = new HashMap<>();

    public static void main(String[] args) {
        staticfiles("webroot");
        addServices();

        try (ServerSocket serverSocket = new ServerSocket(PORT)) {
            System.out.println("Servidor escuchando en el puerto " + PORT);

            while (true) {
                Socket clientSocket = serverSocket.accept();
                new Thread(new ClientHandler(clientSocket)).start();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    public static void addServices() {
        getServices.put("/hello", (req, res) -> "Hello " + req.getValue("name"));
        getServices.put("/pi", (req, res) -> String.valueOf(Math.PI));
    }

    public static void staticfiles(String directory) {
        WEB_ROOT = "target/classes/"+directory;

        Path path = Paths.get(WEB_ROOT);
        try {
            if (!Files.exists(path)) {
                Files.createDirectories(path);
                System.out.println("Directorio creado: " + WEB_ROOT);
            }
        } catch (IOException e) {
            System.err.println("Error al crear el directorio: " + e.getMessage());
        }
    }

    private static class ClientHandler implements Runnable {
        private Socket clientSocket;

        public ClientHandler(Socket clientSocket) {
            this.clientSocket = clientSocket;
        }

        @Override
        public void run() {
            try (BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
                 OutputStream out = clientSocket.getOutputStream()) {

                String requestLine = in.readLine();
                if (requestLine == null) return;

                String[] tokens = requestLine.split(" ");
                if (tokens.length < 3) return;

                String method = tokens[0];
                String requestedResource = tokens[1];
                String basePath = requestedResource.split("\\?")[0];

                if ("GET".equals(method) && getServices.containsKey(basePath)) {
                    Request req = new Request(requestedResource);
                    Response res = new Response(out);
                    String response = getServices.get(basePath).handleREST(req, res);
                    sendResponse(out, response);
                } else {
                    serveStaticFile(basePath, out);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        private void serveStaticFile(String resource, OutputStream out) throws IOException {
            Path filePath = Paths.get(WEB_ROOT, resource);
            if (Files.exists(filePath) && !Files.isDirectory(filePath)) {
                String contentType = Files.probeContentType(filePath);
                byte[] fileContent = Files.readAllBytes(filePath);

                String responseHeader = "HTTP/1.1 200 OK\r\n" +
                        "Content-Type: " + contentType + "\r\n" +
                        "Content-Length: " + fileContent.length + "\r\n" +
                        "\r\n";
                out.write(responseHeader.getBytes());
                out.write(fileContent);
            } else {
                send404(out);
            }
        }

        private void sendResponse(OutputStream out, String response) throws IOException {
            String httpResponse = "HTTP/1.1 200 OK\r\n" +
                    "Content-Type: text/plain\r\n" +
                    "Content-Length: " + response.length() + "\r\n" +
                    "\r\n" +
                    response;
            out.write(httpResponse.getBytes());
        }

        private void send404(OutputStream out) throws IOException {
            String response = "HTTP/1.1 404 Not Found\r\n" +
                    "Content-Type: application/json\r\n" +
                    "\r\n" +
                    "{\"error\": \"Not Found\"}";
            out.write(response.getBytes());
        }
    }
}