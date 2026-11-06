package com.juniverse.numbers.dto;

import java.util.Map;

/**
 * Objeto de transferencia con la información de la petición y la respuesta que se registra.
 *
 * @author Java Universe
 * @param method método de HTTP
 * @param path ruta de la petición
 * @param query cadena de consulta
 * @param requestHeaders encabezados de petición
 * @param requestBody cuerpo de petición
 * @param status código de estado de respuesta
 * @param responseHeaders encabezados de respuesta
 * @param responseBody cuerpo de respuesta
 * @param remoteAddr dirección remota
 */
public record RequestResponseDataDTO(
        String method,
        String path,
        String query,
        Map<String, String> requestHeaders,
        String requestBody,
        int status,
        Map<String, String> responseHeaders,
        String responseBody,
        String remoteAddr) {

}
