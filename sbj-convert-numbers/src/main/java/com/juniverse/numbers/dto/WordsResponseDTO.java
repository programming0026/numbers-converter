package com.juniverse.numbers.dto;

/**
 * Resultado de convertir una cantidad en letras a número.
 *
 * @param text cantidad en letras recibida
 * @param number valor numérico
 * @param timestamp fecha y hora de la conversión
 */
public record WordsResponseDTO(
        String text,
        Long number,
        String timestamp) {

}