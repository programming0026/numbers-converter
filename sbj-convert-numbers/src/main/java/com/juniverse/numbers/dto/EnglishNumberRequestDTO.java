package com.juniverse.numbers.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

/**
 * Los límites cubren todo el rango que el convertidor en inglés puede nombrar sin ambigüedad
 * (hasta {@link Long#MAX_VALUE}, que se mantiene por debajo de 1,000 quintillones).
 *
 * @author ajuar
 * @param number número
 */
public record EnglishNumberRequestDTO(
        @NotNull(message = "The number cannot be null")
        @Min(value = -Long.MAX_VALUE, message = "The number must be greater than or equal to -9223372036854775807")
        @Max(value = Long.MAX_VALUE, message = "The number must be less than or equal to 9223372036854775807")
        Long number) {

}
