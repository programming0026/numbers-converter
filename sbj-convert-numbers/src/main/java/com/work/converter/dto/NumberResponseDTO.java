package com.work.converter.dto;

/**
 * The Record NumberResponseDTO.
 *
 * @author ajuar
 * @param number the number
 * @param words the words
 * @param timestamp the timestamp
 */
public record NumberResponseDTO(
        Long number,
        String words,
        String timestamp) {
	
}
