package com.juniverse.numbers.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

/**
 * @author ajuar
 */
public record EnglishWordsRequestDTO(
        @NotBlank(message = "The text cannot be empty")
        @Size(max = 300, message = "The text must not exceed 300 characters")
        @Pattern(regexp = "^[A-Za-z]+(?:[ -][A-Za-z]+)*$",
                message = "The text may only contain English letters, single spaces and hyphens")
        String text) {

}
