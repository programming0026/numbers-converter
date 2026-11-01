package com.juniverse.numbers.dto;

/**
 * @author ajuar
 */
public record ErrorResponseDTO(String message,
        String error,
        String timestamp,
        String path) {

}
