package edu.escuelaing.arem.ASE.app;

/**
 * Clase que representa una solicitud HTTP.
 * Proporciona métodos para extraer valores de parámetros de consulta.
 */
public class Request {
    private String resource;

    public Request(String resource) {
        this.resource = resource;
    }

    /**
     * Extrae el valor de un parámetro de consulta.
     *
     * @param key Nombre del parámetro.
     * @return Valor del parámetro, o null si no existe.
     */
    public String getValue(String key) {
        String[] parts = resource.split("\\?");
        if (parts.length > 1) {
            String[] params = parts[1].split("&");
            for (String param : params) {
                String[] keyValue = param.split("=");
                if (keyValue.length > 1 && keyValue[0].equals(key)) {
                    return keyValue[1];
                }
            }
        }
        return null;
    }
}