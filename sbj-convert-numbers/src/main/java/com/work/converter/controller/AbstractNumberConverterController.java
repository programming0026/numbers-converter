package com.work.converter.controller;

import com.work.converter.dto.NumberResponseDTO;
import com.work.converter.dto.WordsResponseDTO;
import com.work.converter.enums.Language;
import com.work.converter.exception.ServiceException;
import com.work.converter.service.NumberConverter;
import com.work.converter.service.NumberConverterFactory;
import com.work.converter.util.DateUtil;
import java.time.LocalDateTime;
import java.time.ZoneId;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

/**
 * The Class AbstractNumberConverterController.
 * Base controller that provides the two operations offered by {@link NumberConverter}
 * (obtained through {@link NumberConverterFactory}) for a fixed language. Subclasses
 * expose the actual endpoints using a request DTO validated with annotations specific
 * to their language.
 *
 * @author ajuar
 */
@Slf4j
public abstract class AbstractNumberConverterController {

    /**
     * The number converter resolved from the factory for this controller's language.
     */
    private final NumberConverter numberConverter;

    /**
     * Instantiates a new abstract number converter controller.
     *
     * @param language the language handled by this controller
     */
    protected AbstractNumberConverterController(Language language) {
        this.numberConverter = NumberConverterFactory.of(language);
    }

    /**
     * Convert a number to words.
     *
     * @param number the number
     * @return the response entity
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
     * Convert words to a number.
     *
     * @param text the text
     * @return the response entity
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
