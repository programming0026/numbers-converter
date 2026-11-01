package com.juniverse.numbers.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

/**
 * @author ajuar
 */
public record SpanishWordsRequestDTO(
        @NotBlank(message = "El texto no puede estar vacío")
        @Size(max = 300, message = "El texto no debe exceder los 300 caracteres")
        @Pattern(regexp = "^[A-Za-zÁÉÍÓÚáéíóúÑñÜü]+(?:[ ][A-Za-zÁÉÍÓÚáéíóúÑñÜü]+)*$",
                message = "El texto solo puede contener letras en español y espacios simples")
        String text) {

}
