package com.juniverse.numbers.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

/**
 * Los límites se fijan en 999,999,999,999,999 (999 mil billones) para que el multiplicador «billón»
 * multiplicador se mantenga por debajo de 1,000 y las palabras resultantes no sean ambiguas.
 *
 * @author Java Universe
 * @param number número
 */
public record SpanishNumberRequestDTO(
        @NotNull(message = "El número no puede ser nulo")
        @Min(value = -999999999999999L, message = "El número debe ser mayor o igual a -999,999,999,999,999")
        @Max(value = 999999999999999L, message = "El número debe ser menor o igual a 999,999,999,999,999")
        Long number) {

}
