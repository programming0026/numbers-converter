package com.juniverse.numbers.controller;

import com.juniverse.numbers.dto.NumberResponseDTO;
import com.juniverse.numbers.dto.WordsResponseDTO;
import com.juniverse.numbers.enums.Language;
import com.juniverse.numbers.exception.ServiceException;
import com.juniverse.numbers.service.NumberConverter;
import com.juniverse.numbers.service.NumberConverterFactory;
import com.juniverse.numbers.util.DateUtil;
import java.time.LocalDateTime;
import java.time.ZoneId;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

/**
 * Controlador base que ofrece las dos operaciones de {@link NumberConverter}
 * (obtenido mediante {@link NumberConverterFactory}) para un idioma fijo. Las subclases
 * exponen los endpoints reales con un DTO de petición validado con anotaciones propias
 * a su idioma.
 *
 * @author ajuar
 */
@Slf4j
public abstract class AbstractNumberConverterController {

    /**
     * Convertidor obtenido de la fábrica para el idioma de este controlador.
     */
    private final NumberConverter numberConverter;

    protected AbstractNumberConverterController(Language language) {
        this.numberConverter = NumberConverterFactory.of(language);
    }

    /**
     * Convierte un número a palabras.
     *
     * @param number número
     * @return entidad de respuesta
     */
    protected ResponseEntity<NumberResponseDTO> convertNumberToWords(Long number) {
        try {
            String words = numberConverter.toWords(number);
            NumberResponseDTO response = new NumberResponseDTO(
                    number,
                    words,
                    DateUtil.getFormat(LocalDateTime.now(ZoneId.systemDefault()))
            );
            return ResponseEntity.ok(response);
        } catch (Exception ex) {
            log.error("Error converting number: {}", number, ex);
            throw new ServiceException(HttpStatus.INTERNAL_SERVER_ERROR,
                    "An error occurred while converting the number: " + number, ex);
        }
    }

    /**
     * Convierte palabras a número.
     *
     * @param text texto
     * @return entidad de respuesta
     */
    protected ResponseEntity<WordsResponseDTO> convertWordsToNumber(String text) {
        try {
            long number = numberConverter.toNumber(text);
            WordsResponseDTO response = new WordsResponseDTO(
                    text,
                    number,
                    DateUtil.getFormat(LocalDateTime.now(ZoneId.systemDefault()))
            );
            return ResponseEntity.ok(response);
        } catch (IllegalArgumentException ex) {
            log.error("Error converting words: {}", text, ex);
            throw new ServiceException(HttpStatus.BAD_REQUEST, ex.getMessage(), ex);
        } catch (Exception ex) {
            log.error("Error converting words: {}", text, ex);
            throw new ServiceException(HttpStatus.INTERNAL_SERVER_ERROR,
                    "An error occurred while converting the words: " + text, ex);
        }
    }
}
