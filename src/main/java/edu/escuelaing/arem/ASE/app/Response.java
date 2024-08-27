package edu.escuelaing.arem.ASE.app;

import java.io.OutputStream;

/**
 * Clase que representa una respuesta HTTP.
 * Proporciona métodos para construir y enviar la respuesta.
 */
public class Response {
    private OutputStream outputStream;

    public Response(OutputStream outputStream) {
        this.outputStream = outputStream;
    }
}
