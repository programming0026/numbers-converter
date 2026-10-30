package com.juniverse.numbers.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

/**
 * The Record EnglishNumberRequestDTO.
 * Bounds match the full range the English converter can name unambiguously
 * (up to {@link Long#MAX_VALUE}, since that stays below 1,000 quintillion).
 *
 * @author ajuar
 * @param number the number
 */
public record EnglishNumberRequestDTO(
        @NotNull(message = "The number cannot be null")
        @Min(value = -Long.MAX_VALUE, message = "The number must be greater than or equal to -9223372036854775807")
        @Max(value = Long.MAX_VALUE, message = "The number must be less than or equal to 9223372036854775807")
        Long number) {

}
