package edu.escuelaing.arem.ASE.app;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.OutputStream;
import java.net.Socket;

public class RestServiceImpl implements RESTService {

    @Override
    public void handleGet(String[] requestLine, BufferedReader in, OutputStream out, Socket clientSocket) throws IOException {
        System.out.println();
        String requestUrl = "";
        String requestMethod = "";
        String remoteAddress = clientSocket.getRemoteSocketAddress().toString();
        String referrerPolicy = "strict-origin-when-cross-origin";
        String statusCode = "200 OK";
        String header = in.readLine();
        if (header != null) {
            String[] tokens = header.split(" ");
            if (tokens.length >= 2) {

                requestMethod = requestLine[0];
                requestUrl = tokens[1];
            }
        }

        String jsonResponse = String.format("{" +
                "\"Request URL\": \"%s\"," +
                "\"Request Method\": \"%s\"," +
                "\"Status Code\": \"%s\"," +
                "\"Remote Address\": \"%s\"," +
                "\"Referrer Policy\": \"%s\"" +
                "}", requestUrl, requestMethod, statusCode, remoteAddress, referrerPolicy);

        sendJsonResponse(out, jsonResponse);
    }


    public void handlePost( BufferedReader in, OutputStream out) throws IOException {
        // Leer los encabezados adicionales
        String line;
        int contentLength = -1;
        while ((line = in.readLine()) != null && !line.isEmpty()) {
            if (line.startsWith("Content-Length:")) {
                contentLength = Integer.parseInt(line.split(":")[1].trim());
            }
        }

        // Leer el cuerpo
        StringBuilder body = new StringBuilder();
        if (contentLength > 0) {
            char[] buffer = new char[contentLength];
            in.read(buffer, 0, contentLength);
            body.append(buffer);
        }

        String message = extractValue(body,"message");
        if (message == null) return;

        String jsonResponse = String.format("{ \"status\": \"Message received\", \"message\": \"%s\" }", message);
        sendJsonResponse(out, jsonResponse);
    }


    @Override
    public void handlePut(BufferedReader in, OutputStream out) throws IOException {
        // Implementación similar a POST
    }
    @Override
    public void handleDelete(BufferedReader in, OutputStream out) throws IOException {
        // Implementación similar a GET o POST
    }

    private void sendJsonResponse(OutputStream out, String jsonResponse) throws IOException {
        String responseHeader = "HTTP/1.1 200 OK\r\n" +
                "Content-Type: application/json\r\n" +
                "Content-Length: " + jsonResponse.length() + "\r\n" +
                "\r\n";
        out.write(responseHeader.getBytes());
        out.write(jsonResponse.getBytes());
    }
    public static String extractValue(StringBuilder jsonString, String key) {
        // Encontrar la posiciÃ³n de la clave
        int startIndex = jsonString.indexOf("\"" + key + "\":\"") + key.length() + 4;
        int endIndex = jsonString.indexOf("\"", startIndex);

        // Extraer el valor entre las comillas
        return jsonString.substring(startIndex, endIndex);
    }
}