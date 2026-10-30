package com.juniverse.numbers.dto;

/**
 * The Record ErrorResponseDTO.
 *
 * @author ajuar
 * @param message the message
 * @param error the error
 * @param timestamp the timestamp
 * @param path the path
 */
public record ErrorResponseDTO(String message,
        String error,
        String timestamp,
        String path) {

}
