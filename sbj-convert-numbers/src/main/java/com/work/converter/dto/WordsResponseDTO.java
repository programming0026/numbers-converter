package com.work.converter.dto;

/**
 * The Record WordsResponseDTO.
 *
 * @author ajuar
 * @param text the quantity written in words that was received
 * @param number the numeric value resolved from the text
 * @param timestamp the timestamp
 */
public record WordsResponseDTO(
        String text,
        Long number,
        String timestamp) {

}