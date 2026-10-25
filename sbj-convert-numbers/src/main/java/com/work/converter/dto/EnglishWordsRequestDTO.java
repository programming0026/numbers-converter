package com.work.converter.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

/**
 * The Record EnglishWordsRequestDTO.
 *
 * @author ajuar
 * @param text the quantity written in English words, e.g. "two thousand five hundred"
 */
public record EnglishWordsRequestDTO(
        @NotBlank(message = "The text cannot be empty")
        @Size(max = 300, message = "The text must not exceed 300 characters")
        @Pattern(regexp = "^[A-Za-z]+(?:[ -][A-Za-z]+)*$",
                message = "The text may only contain English letters, single spaces and hyphens")
        String text) {

}
