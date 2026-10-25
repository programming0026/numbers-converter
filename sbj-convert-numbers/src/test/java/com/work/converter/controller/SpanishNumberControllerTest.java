package com.work.converter.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.work.converter.dto.SpanishNumberRequestDTO;
import com.work.converter.dto.SpanishWordsRequestDTO;
import com.work.converter.util.Constants;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * The Class SpanishNumberControllerTest.
 * Integration tests for {@link SpanishNumberController}.
 *
 * @author ajuar
 */
@SpringBootTest
@AutoConfigureMockMvc
class SpanishNumberControllerTest {

    private static final long MIN_ALLOWED = -999_999_999_999_999L;
    private static final long MAX_ALLOWED = 999_999_999_999_999L;

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    /**
     * Test convert number to words successfully.
     *
     * @throws Exception the exception
     */
    @Test
    void testConvertNumberToWordsSuccess() throws Exception {
        SpanishNumberRequestDTO request = new SpanishNumberRequestDTO(123L);

        mockMvc.perform(post("/v1/numbers/es/convert")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(header().exists(Constants.HEADER_TRANSACTION_ID_KEY))
                .andExpect(jsonPath("$.number").value(123))
                .andExpect(jsonPath("$.words").value("ciento veintitrés"));
    }

    /**
     * Test convert negative number to words successfully.
     *
     * @throws Exception the exception
     */
    @Test
    void testConvertNumberToWordsNegativeSuccess() throws Exception {
        SpanishNumberRequestDTO request = new SpanishNumberRequestDTO(-45L);

        mockMvc.perform(post("/v1/numbers/es/convert")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.words").value("menos cuarenta y cinco"));
    }

    /**
     * Test a null number is rejected by bean validation.
     *
     * @throws Exception the exception
     */
    @Test
    void testConvertNumberToWordsNullNumberReturnsValidationError() throws Exception {
        mockMvc.perform(post("/v1/numbers/es/convert")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("VALIDATION_ERROR"))
                .andExpect(jsonPath("$.message").value("El número no puede ser nulo"));
    }

    /**
     * Test a number below the allowed lower bound is rejected.
     *
     * @throws Exception the exception
     */
    @Test
    void testConvertNumberToWordsBelowMinimumReturnsValidationError() throws Exception {
        SpanishNumberRequestDTO request = new SpanishNumberRequestDTO(MIN_ALLOWED - 1);

        mockMvc.perform(post("/v1/numbers/es/convert")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("VALIDATION_ERROR"))
                .andExpect(jsonPath("$.message")
                        .value("El número debe ser mayor o igual a -999,999,999,999,999"));
    }

    /**
     * Test a number above the allowed upper bound is rejected.
     *
     * @throws Exception the exception
     */
    @Test
    void testConvertNumberToWordsAboveMaximumReturnsValidationError() throws Exception {
        SpanishNumberRequestDTO request = new SpanishNumberRequestDTO(MAX_ALLOWED + 1);

        mockMvc.perform(post("/v1/numbers/es/convert")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("VALIDATION_ERROR"))
                .andExpect(jsonPath("$.message")
                        .value("El número debe ser menor o igual a 999,999,999,999,999"));
    }

    /**
     * Test the boundary values are accepted.
     *
     * @throws Exception the exception
     */
    @Test
    void testConvertNumberToWordsBoundaryValuesAccepted() throws Exception {
        mockMvc.perform(post("/v1/numbers/es/convert")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(new SpanishNumberRequestDTO(MIN_ALLOWED))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.number").value(MIN_ALLOWED));

        mockMvc.perform(post("/v1/numbers/es/convert")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(new SpanishNumberRequestDTO(MAX_ALLOWED))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.number").value(MAX_ALLOWED));
    }

    /**
     * Test an invalid JSON payload returns a unified bad request error.
     *
     * @throws Exception the exception
     */
    @Test
    void testConvertNumberToWordsInvalidFormatReturnsBadRequest() throws Exception {
        mockMvc.perform(post("/v1/numbers/es/convert")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"number\": \"invalid_text\"}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("INVALID_INPUT_FORMAT"));
    }

    /**
     * Test convert words to a number successfully.
     *
     * @throws Exception the exception
     */
    @Test
    void testConvertWordsToNumberSuccess() throws Exception {
        SpanishWordsRequestDTO request = new SpanishWordsRequestDTO("dos mil quinientos");

        mockMvc.perform(post("/v1/numbers/es/to-number")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.number").value(2500));
    }

    /**
     * Test convert accented words to a number successfully.
     *
     * @throws Exception the exception
     */
    @Test
    void testConvertWordsToNumberWithAccentsSuccess() throws Exception {
        SpanishWordsRequestDTO request = new SpanishWordsRequestDTO("ciento veintitrés");

        mockMvc.perform(post("/v1/numbers/es/to-number")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.number").value(123));
    }

    /**
     * Test blank text is rejected by bean validation.
     *
     * @throws Exception the exception
     */
    @Test
    void testConvertWordsToNumberBlankReturnsValidationError() throws Exception {
        SpanishWordsRequestDTO request = new SpanishWordsRequestDTO("");

        mockMvc.perform(post("/v1/numbers/es/to-number")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("VALIDATION_ERROR"));
    }

    /**
     * Test text containing characters outside the allowed pattern is rejected.
     *
     * @throws Exception the exception
     */
    @Test
    void testConvertWordsToNumberInvalidCharactersReturnsValidationError() throws Exception {
        SpanishWordsRequestDTO request = new SpanishWordsRequestDTO("dos mil 500");

        mockMvc.perform(post("/v1/numbers/es/to-number")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("VALIDATION_ERROR"))
                .andExpect(jsonPath("$.message")
                        .value("El texto solo puede contener letras en español y espacios simples"));
    }

    /**
     * Test text exceeding the maximum length is rejected.
     *
     * @throws Exception the exception
     */
    @Test
    void testConvertWordsToNumberTooLongReturnsValidationError() throws Exception {
        SpanishWordsRequestDTO request = new SpanishWordsRequestDTO("a".repeat(301));

        mockMvc.perform(post("/v1/numbers/es/to-number")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("VALIDATION_ERROR"))
                .andExpect(jsonPath("$.message").value("El texto no debe exceder los 300 caracteres"));
    }

    /**
     * Test a syntactically valid but unrecognized word is rejected by the service layer.
     *
     * @throws Exception the exception
     */
    @Test
    void testConvertWordsToNumberUnrecognizedWordReturnsBadRequest() throws Exception {
        SpanishWordsRequestDTO request = new SpanishWordsRequestDTO("banana");

        mockMvc.perform(post("/v1/numbers/es/to-number")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("ERROR"))
                .andExpect(jsonPath("$.message").value("Palabra no reconocida: 'banana'"));
    }
}
