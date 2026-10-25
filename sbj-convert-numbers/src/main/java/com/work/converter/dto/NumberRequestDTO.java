package com.work.converter.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

/**
 * The Record NumberRequestDTO.
 *
 * @author ajuar
 * @param number the number
 */
public record NumberRequestDTO(
        @NotNull(message = "El número no puede ser nulo")
        @Min(value = -999999999, message = "El número debe ser mayor o igual a -999,999,999")
        @Max(value = 999999999, message = "El número debe ser menor o igual a 999,999,999")
        Long number) {
	
}
