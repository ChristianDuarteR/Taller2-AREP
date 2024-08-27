package edu.escuelaing.arem.ASE.app;

import com.google.gson.Gson;
import edu.escuelaing.arem.ASE.app.exceptions.ServiceNotFoundException;

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
    private static final Map<String, RESTService> services = new HashMap<>();

    public static void main(String[] args) {
        staticfiles("webroot");
        startServices();

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

    public static void get(String path, RESTService action) {
        services.put("/api" + path, action);
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

    public static void startServices(){
        get("", (req, resp) ->"API WORKING");

        get("/greet", (req, res) -> {
            String name = req.getValue("name");
            String greeting = req.getValue("greeting");
            if (name != null && greeting != null) {
                return greeting + " " + name;
            } else {
                return "Hello, World!";
            }
        });

        get("/calculate", (req, res) -> {

            String operation = req.getValue("operation");
            String num1 = req.getValue("num1");
            String num2 = req.getValue("num2");

            if (operation != null && num1 != null && num2 != null) {
                try {
                    double a = Double.parseDouble(num1);
                    double b = Double.parseDouble(num2);
                    switch (operation) {
                        case "add":
                            return String.valueOf(a + b);
                        case "subtract":
                            return String.valueOf(a - b);
                        case "multiply":
                            return String.valueOf(a * b);
                        case "divide":
                            return b != 0 ? String.valueOf(a / b) : "Cannot divide by zero";
                        default:
                            return "Invalid operation";
                    }
                } catch (NumberFormatException e) {
                    return "Invalid number format";
                }
            }
            return "Missing parameters";
        });

        get("/system-info", (req, res) -> {
            Map<String, Object> systemInfo = new HashMap<>();
            systemInfo.put("OS", System.getProperty("os.name"));
            systemInfo.put("OS Version", System.getProperty("os.version"));
            systemInfo.put("Architecture", System.getProperty("os.arch"));
            systemInfo.put("Java Version", System.getProperty("java.version"));
            systemInfo.put("JRE Vendor", System.getProperty("java.vendor"));
            systemInfo.put("JRE Home", System.getProperty("java.home"));
            systemInfo.put("Available Processors", Runtime.getRuntime().availableProcessors());
            systemInfo.put("Free Memory (bytes)", Runtime.getRuntime().freeMemory());
            systemInfo.put("Total Memory (bytes)", Runtime.getRuntime().totalMemory());
            systemInfo.put("Max Memory (bytes)", Runtime.getRuntime().maxMemory());

            res.setContentType("application/json");
            return new Gson().toJson(systemInfo);
        });

        get("/index", (req, res) -> {
            res.setContentType("text/html");
            return "<html>" +
                        "<head><title>Taller dos Simulando Spark</title></head>" +
                            "<body>" +
                                "<h1>Creando un FrameWork</h1>" +
                                "<p>Bienvenido a la API SimpleWebServer</p>" +
                                "<p>Recuerde que antes de cualquier llamado GET debe preceder /API/ antes de su solicitud</p>" +
                                "<h2>A continuacion se listan los servicios para que sean probados:</h2>" +
                                "<ul>" +
                                    "<li><a href=\"/api/greet?name=?&greeting=?\">/api/greet?name=?&greeting=?</a> - Devuelve un saludo personalizado.</li>" +
                                    "<li><a href=\"/api/calculate?operation=?&num1=?&num2=?\">/api/calculate?operation=?&num1=?&num2=?</a> - Calculadora con operaciones como (add, subtract, multiply,divide )." +
                                    "</li>" +
                                "<li><a href=\"/api/system-info\">/api/system-info</a> - Devuelve informacion del sistema.</li>" +
                                "<li><a href=\"/api/index\">/api/index</a> - Devuelve esta pagina.</li>" +
                                "<li><a href=\"/api\">/api</a> - Prueba para confirmar que API is working </li>" +
                                "</ul>" +
                            "</body>" +
                    "</html>";
        });

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

                //String method = tokens[0];
                String requestedResource = tokens[1];
                String basePath = requestedResource.split("\\?")[0];

                if(basePath.startsWith("/api")) {
                    if (services.containsKey(basePath)) {
                        Request req = new Request(requestedResource);
                        Response res = new Response(out);
                        res.setCodeResponse("200 OK");
                        String response = services.get(basePath).handleREST(req, res);
                        sendResponse(out, response, res);
                    } else {
                        throw new ServiceNotFoundException("No service found for path: " + basePath);
                    }
                }else {
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

        private void sendResponse(OutputStream out, String response, Response res) throws IOException {
            String httpResponse = "HTTP/1.1 "+ res.getCodeResponse()+ "\r\n" +
                    "Content-Type: " + res.getContentType() + "\r\n" +
                    "Content-Length: " + response.length() + "\r\n" +
                    "\r\n" +
                    response;
            out.write(httpResponse.getBytes());
            out.flush();
        }

        private void send404(OutputStream out) throws IOException {
            String response = "HTTP/1.1 404 Not Found\r\n" +
                    "Content-Type: application/json\r\n" +
                    "\r\n" +
                    "{\"error\": \"Not Found\"}";
            out.write(response.getBytes());
            out.flush();
        }
    }
}