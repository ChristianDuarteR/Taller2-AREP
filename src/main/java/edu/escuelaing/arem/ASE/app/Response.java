package edu.escuelaing.arem.ASE.app;

import java.io.OutputStream;

/**
 * Clase que representa una respuesta HTTP.
 * Proporciona métodos para construir y enviar la respuesta.
 */
public class Response {
    private OutputStream outputStream;
    private String contentType;
    private String codeResponse;

    /**
     * Constructor que inicializa el objeto Response con un OutputStream.
     * @param outputStream Stream de salida donde se escribirá la respuesta HTTP.
     */
    public Response(OutputStream outputStream) {
        this.outputStream = outputStream;
        this.contentType = "text/plain";
    }

    /**
     * Establece el tipo de contenido de la respuesta HTTP.
     * @param contentType El tipo de contenido, como "text/html" o "application/json".
     */
    public void setContentType(String contentType) {
        this.contentType = contentType;
    }

    /**
     * Devuelve el tipo de contenido de la respuesta HTTP.
     * @return El tipo de contenido actual de la respuesta.
     */
    public String getContentType() {
        return contentType;
    }

    /**
     * Establece el código de estado de la respuesta HTTP.
     * @param codeResponse El código de estado, como "200 OK" o "404 Not Found".
     */
    public void setCodeResponse(String codeResponse) {
        this.codeResponse = codeResponse;
    }

    /**
     * Devuelve el código de estado de la respuesta HTTP.
     * @return El código de estado actual de la respuesta.
     */
    public String getCodeResponse() {
        return codeResponse;
    }
}
