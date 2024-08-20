package edu.escuelaing.arem.ASE.app;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.OutputStream;
import java.net.Socket;

/**
 * Implementación de los servicios REST para el servidor web.
 * Maneja solicitudes HTTP GET y POST.
 */
public class RestServiceImpl implements RESTService {

    /**
     * Maneja las solicitudes HTTP GET.
     * Genera una respuesta en formato JSON con detalles sobre la solicitud.
     *
     * @param requestLine Línea de solicitud HTTP.
     * @param in BufferedReader para leer los encabezados de la solicitud.
     * @param out Salida de datos del socket del cliente.
     * @param clientSocket Socket del cliente.
     * @throws IOException Si ocurre un error de entrada/salida.
     */
    @Override
    public void handleGet(String[] requestLine, BufferedReader in, OutputStream out, Socket clientSocket) throws IOException {
        String requestUrl = "";
        String requestMethod = "";
        String remoteAddress = clientSocket.getRemoteSocketAddress().toString();
        String referrerPolicy = "strict-origin-when-cross-origin";
        String statusCode = "200 OK";

        // Lee el encabezado de la solicitud.
        String header = in.readLine();
        if (header != null) {
            String[] tokens = header.split(" ");
            if (tokens.length >= 2) {
                requestMethod = requestLine[0];
                requestUrl = tokens[1];
            }
        }

        // Crea una respuesta en formato JSON.
        String jsonResponse = String.format("{" +
                "\"Request URL\": \"%s\"," +
                "\"Request Method\": \"%s\"," +
                "\"Status Code\": \"%s\"," +
                "\"Remote Address\": \"%s\"," +
                "\"Referrer Policy\": \"%s\"" +
                "}", requestUrl, requestMethod, statusCode, remoteAddress, referrerPolicy);

        sendJsonResponse(out, 200, "OK", jsonResponse);
    }

    /**
     * Maneja las solicitudes HTTP POST.
     * Lee el cuerpo de la solicitud y responde con un JSON que confirma la recepción del mensaje.
     *
     * @param in BufferedReader para leer los encabezados y el cuerpo de la solicitud.
     * @param out Salida de datos del socket del cliente.
     * @throws IOException Si ocurre un error de entrada/salida.
     */
    public void handlePost(BufferedReader in, OutputStream out) throws IOException {
        String line;
        int contentLength = -1;

        while ((line = in.readLine()) != null && !line.isEmpty()) {
            if (line.startsWith("Content-Length:")) {
                contentLength = Integer.parseInt(line.split(":")[1].trim());
            }
        }

        StringBuilder body = new StringBuilder();
        if (contentLength > 0) {
            char[] buffer = new char[contentLength];
            in.read(buffer, 0, contentLength);
            body.append(buffer);
        }

        String message = extractValue(body, "message");
        if (message == null) return;

        String jsonResponse = String.format("{ \"status\": \"Message received\", \"message\": \"%s\" }", message);
        sendJsonResponse(out, 201, "Created", jsonResponse);
    }

    /**
     * Envía una respuesta HTTP en formato JSON al cliente.
     *
     * @param out Salida de datos del socket del cliente.
     * @param statusCode Código de estado HTTP.
     * @param statusMessage Mensaje de estado HTTP.
     * @param jsonResponse Cuerpo de la respuesta en formato JSON.
     * @throws IOException Si ocurre un error de entrada/salida.
     */
    private void sendJsonResponse(OutputStream out, int statusCode, String statusMessage, String jsonResponse) throws IOException {
        String httpResponse = String.format(
                "HTTP/1.1 %d %s\r\n" +
                        "Content-Type: application/json\r\n" +
                        "Content-Length: %d\r\n" +
                        "\r\n" +
                        "%s",
                statusCode, statusMessage, jsonResponse.length(), jsonResponse
        );
        out.write(httpResponse.getBytes());
        out.flush();
    }

    /**
     * Extrae el valor asociado a una clave específica en una cadena JSON.
     *
     * @param jsonString Cadena JSON que contiene la clave y valor.
     * @param key Clave cuyo valor se desea extraer.
     * @return Valor asociado a la clave, o null si la clave no se encuentra.
     */
    public static String extractValue(StringBuilder jsonString, String key) {
        int startIndex = jsonString.indexOf("\"" + key + "\":\"");
        if (startIndex == -1) {
            return null;
        }
        startIndex += key.length() + 4;
        int endIndex = jsonString.indexOf("\"", startIndex);

        if (endIndex == -1) {
            return null;
        }

        return jsonString.substring(startIndex, endIndex);
    }
}
