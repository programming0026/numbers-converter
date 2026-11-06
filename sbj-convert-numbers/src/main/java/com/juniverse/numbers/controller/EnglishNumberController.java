package com.juniverse.numbers.controller;

import com.juniverse.numbers.dto.EnglishNumberRequestDTO;
import com.juniverse.numbers.dto.EnglishWordsRequestDTO;
import com.juniverse.numbers.dto.ErrorResponseDTO;
import com.juniverse.numbers.dto.NumberResponseDTO;
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
 * Expone la conversión entre números y palabras en inglés, con el convertidor obtenido
 * de {@link com.juniverse.numbers.service.NumberConverterFactory}.
 *
 * @author Java Universe
 */
@RestController
@RequestMapping("/v1/numbers/en")
public class EnglishNumberController extends AbstractNumberConverterController {

    public EnglishNumberController() {
        super(Language.EN);
    }

    /**
     * Convierte un número a palabras en inglés.
     *
     * @param request petición
     * @return entidad de respuesta
     */
    @PostMapping(path = "/convert", produces = {MediaType.APPLICATION_JSON_VALUE})
    @Operation(summary = "convertNumberToWords", description = "Convert number to English words")
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
            @Valid @RequestBody EnglishNumberRequestDTO request) {
        return convertNumberToWords(request.number());
    }

    /**
     * Convierte palabras en inglés a número.
     *
     * @param request petición
     * @return entidad de respuesta
     */
    @PostMapping(path = "/to-number", produces = {MediaType.APPLICATION_JSON_VALUE})
    @Operation(summary = "convertWordsToNumber", description = "Convert English words to number")
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
            @Valid @RequestBody EnglishWordsRequestDTO request) {
        return convertWordsToNumber(request.text());
    }
}
