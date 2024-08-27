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

    public Response(OutputStream outputStream) {
        this.outputStream = outputStream;
        this.contentType = "text/plain";
    }

    public void setContentType(String contentType) {
        this.contentType = contentType;
    }

    public String getContentType() {
        return contentType;
    }

    public void setCodeResponse(String codeResponse){
        this.codeResponse = codeResponse;
    }

    public String getCodeResponse() {
        return codeResponse;
    }
}
