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

    // Puerto en el que el servidor escuchará las conexiones entrantes.
    private static final int PORT = 8080;

    // Directorio raíz donde se encuentran los archivos estáticos.
    private static final String WEB_ROOT = "src/main/webroot";

    // Mapa que almacena los servicios REST disponibles.
    private static final Map<String, RESTService> services = new HashMap<>();

    /**
     * Método principal que inicia el servidor web.
     * Configura el servidor para escuchar en el puerto definido y
     * acepta conexiones entrantes.
     *
     * @param args Argumentos de la línea de comandos.
     */
    public static void main(String[] args) {
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

    /**
     * Configura los servicios REST disponibles en el servidor.
     * En este caso, se agregan los servicios GET y POST.
     */
    public static void addServices() {
        RestServiceImpl services = new RestServiceImpl();
        SimpleWebServer.services.put("GET" , services);
        SimpleWebServer.services.put("POST" , services);
    }

    /**
     * Clase interna que maneja las solicitudes de los clientes en un hilo separado.
     */
    private static class ClientHandler implements Runnable {
        private Socket clientSocket;

        /**
         * Constructor que inicializa el manejador con el socket del cliente.
         *
         * @param clientSocket Socket del cliente.
         */
        public ClientHandler(Socket clientSocket) {
            this.clientSocket = clientSocket;
        }

        /**
         * Método que maneja la solicitud del cliente.
         * Lee la línea de solicitud, determina el método HTTP y el recurso solicitado,
         * y decide si debe manejar una solicitud REST o servir un archivo estático.
         */
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

                if (services.containsKey(method) && requestedResource.startsWith("/api")) {
                    RESTService service = services.get(method);
                    switch (method) {
                        case "GET":
                            service.handleGet(tokens, in, out, clientSocket);
                            break;
                        case "POST":
                            service.handlePost(in, out);
                            break;
                        default:
                            send404(out);
                    }
                } else {
                    serveStaticFile(requestedResource, out);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        /**
         * Imprime los encabezados de la solicitud HTTP.
         *
         * @param requestLine Línea de solicitud HTTP.
         * @param in BufferedReader para leer los encabezados.
         * @throws IOException Si ocurre un error de entrada/salida.
         */
        private void printRequestHeader(String requestLine, BufferedReader in) throws IOException {
            System.out.println("Request Line: " + requestLine);
            String inputLine = "";
            while ((inputLine = in.readLine()) != null) {
                System.out.println("Header: " + inputLine);
                if (!in.ready()) {
                    break;
                }
            }
        }

        /**
         * Sirve un archivo estático al cliente.
         *
         * @param resource Recurso solicitado.
         * @param out Salida de datos del socket del cliente.
         * @throws IOException Si ocurre un error de entrada/salida.
         */
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

        /**
         * Envía una respuesta 404 Not Found al cliente.
         *
         * @param out Salida de datos del socket del cliente.
         * @throws IOException Si ocurre un error de entrada/salida.
         */
        private void send404(OutputStream out) throws IOException {
            String response = "HTTP/1.1 404 Not Found\r\n" +
                    "Content-Type: application/json\r\n" +
                    "\r\n" +
                    "{\"error\": \"Not Found\"}";
            out.write(response.getBytes());
        }
    }
}
