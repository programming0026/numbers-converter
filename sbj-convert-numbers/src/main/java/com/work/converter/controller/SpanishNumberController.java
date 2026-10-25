package com.work.converter.controller;

import com.work.converter.dto.ErrorResponseDTO;
import com.work.converter.dto.NumberResponseDTO;
import com.work.converter.dto.SpanishNumberRequestDTO;
import com.work.converter.dto.SpanishWordsRequestDTO;
import com.work.converter.dto.WordsResponseDTO;
import com.work.converter.enums.Language;
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
 * The Class SpanishNumberController.
 * Exposes number/words conversion for Spanish, backed by the converter resolved
 * from {@link com.work.converter.service.NumberConverterFactory}.
 *
 * @author ajuar
 */
@RestController
@RequestMapping("/v1/numbers/es")
public class SpanishNumberController extends AbstractNumberConverterController {

    /**
     * Instantiates a new spanish number controller.
     */
    public SpanishNumberController() {
        super(Language.ES);
    }

    /**
     * Convert a number to Spanish words.
     *
     * @param request the request
     * @return the response entity
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
     * Convert Spanish words to a number.
     *
     * @param request the request
     * @return the response entity
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
