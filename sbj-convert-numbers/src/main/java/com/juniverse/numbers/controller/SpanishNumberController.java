package com.juniverse.numbers.controller;

import com.juniverse.numbers.dto.ErrorResponseDTO;
import com.juniverse.numbers.dto.NumberResponseDTO;
import com.juniverse.numbers.dto.SpanishNumberRequestDTO;
import com.juniverse.numbers.dto.SpanishWordsRequestDTO;
import com.juniverse.numbers.dto.WordsResponseDTO;
import com.juniverse.numbers.enums.Language;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.Valid;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Expone la conversión entre números y palabras en español, con el convertidor obtenido
 * de {@link com.juniverse.numbers.service.NumberConverterFactory}.
 *
 * @author ajuar
 */
@RestController
@RequestMapping("/v1/numbers/es")
public class SpanishNumberController extends AbstractNumberConverterController {

    public SpanishNumberController() {
        super(Language.ES);
    }

    /**
     * Convierte un número a palabras en español.
     *
     * @param request petición
     * @return entidad de respuesta
     */
    @PostMapping(path = "/convert", produces = {MediaType.APPLICATION_JSON_VALUE})
    @Operation(summary = "convertNumberToWords", description = "Convert number to Spanish words")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Successful operation",
                content = @Content(schema = @Schema(implementation = NumberResponseDTO.class)
                )),
        @ApiResponse(responseCode = "400", description = "Invalid data",
                content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class)
                )),
        @ApiResponse(responseCode = "500", description = "General error",
                content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class)
                ))
    })
    public ResponseEntity<NumberResponseDTO> convertNumberToWords(
            @Valid @RequestBody SpanishNumberRequestDTO request) {
        return convertNumberToWords(request.number());
    }

    /**
     * Convierte palabras en español a número.
     *
     * @param request petición
     * @return entidad de respuesta
     */
    @PostMapping(path = "/to-number", produces = {MediaType.APPLICATION_JSON_VALUE})
    @Operation(summary = "convertWordsToNumber", description = "Convert Spanish words to number")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Successful operation",
                content = @Content(schema = @Schema(implementation = WordsResponseDTO.class)
                )),
        @ApiResponse(responseCode = "400", description = "Invalid data",
                content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class)
                )),
        @ApiResponse(responseCode = "500", description = "General error",
                content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class)
                ))
    })
    public ResponseEntity<WordsResponseDTO> convertWordsToNumber(
            @Valid @RequestBody SpanishWordsRequestDTO request) {
        return convertWordsToNumber(request.text());
    }
}
