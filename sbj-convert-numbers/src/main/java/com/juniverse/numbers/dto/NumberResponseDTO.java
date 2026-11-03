package com.juniverse.numbers.dto;

/**
 * Resultado de convertir un número a letras.
 *
 * @param number número recibido
 * @param words el número escrito en letras
 * @param timestamp fecha y hora de la conversión
 */
public record NumberResponseDTO(
        Long number,
        String words,
        String timestamp) {
	
}
