package com.work.converter.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

/**
 * The Record SpanishNumberRequestDTO.
 * Bounds are capped at 999,999,999,999,999 (999 mil billones) so the "billón"
 * multiplier stays below 1,000 and the resulting words stay unambiguous.
 *
 * @author ajuar
 * @param number the number
 */
public record SpanishNumberRequestDTO(
        @NotNull(message = "El número no puede ser nulo")
        @Min(value = -999999999999999L, message = "El número debe ser mayor o igual a -999,999,999,999,999")
        @Max(value = 999999999999999L, message = "El número debe ser menor o igual a 999,999,999,999,999")
        Long number) {

}
