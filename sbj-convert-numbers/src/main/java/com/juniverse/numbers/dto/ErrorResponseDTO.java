package com.juniverse.numbers.dto;

/**
 * Respuesta de error de la API.
 *
 * @param message descripción del error
 * @param error código del error
 * @param timestamp fecha y hora del error
 * @param path ruta de la petición que falló
 */
public record ErrorResponseDTO(String message,
        String error,
        String timestamp,
        String path) {

}
