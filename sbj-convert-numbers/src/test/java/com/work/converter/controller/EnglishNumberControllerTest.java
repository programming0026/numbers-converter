package com.work.converter.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.work.converter.dto.EnglishNumberRequestDTO;
import com.work.converter.dto.EnglishWordsRequestDTO;
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
 * The Class EnglishNumberControllerTest.
 * Integration tests for {@link EnglishNumberController}.
 *
 * @author ajuar
 */
@SpringBootTest
@AutoConfigureMockMvc
class EnglishNumberControllerTest {

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
        EnglishNumberRequestDTO request = new EnglishNumberRequestDTO(123L);

        mockMvc.perform(post("/v1/numbers/en/convert")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(header().exists(Constants.HEADER_TRANSACTION_ID_KEY))
                .andExpect(jsonPath("$.number").value(123))
                .andExpect(jsonPath("$.words").value("one hundred twenty-three"));
    }

    /**
     * Test convert negative number to words successfully.
     *
     * @throws Exception the exception
     */
    @Test
    void testConvertNumberToWordsNegativeSuccess() throws Exception {
        EnglishNumberRequestDTO request = new EnglishNumberRequestDTO(-45L);

        mockMvc.perform(post("/v1/numbers/en/convert")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.words").value("minus forty-five"));
    }

    /**
     * Test a null number is rejected by bean validation.
     *
     * @throws Exception the exception
     */
    @Test
    void testConvertNumberToWordsNullNumberReturnsValidationError() throws Exception {
        mockMvc.perform(post("/v1/numbers/en/convert")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("VALIDATION_ERROR"))
                .andExpect(jsonPath("$.message").value("The number cannot be null"));
    }

    /**
     * Test a number below the allowed lower bound is rejected.
     *
     * @throws Exception the exception
     */
    @Test
    void testConvertNumberToWordsBelowMinimumReturnsValidationError() throws Exception {
        EnglishNumberRequestDTO request = new EnglishNumberRequestDTO(Long.MIN_VALUE);

        mockMvc.perform(post("/v1/numbers/en/convert")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("VALIDATION_ERROR"))
                .andExpect(jsonPath("$.message")
                        .value("The number must be greater than or equal to -9223372036854775807"));
    }

    /**
     * Test Long.MAX_VALUE, the upper bound, is accepted.
     *
     * @throws Exception the exception
     */
    @Test
    void testConvertNumberToWordsMaxBoundaryAccepted() throws Exception {
        EnglishNumberRequestDTO request = new EnglishNumberRequestDTO(Long.MAX_VALUE);

        mockMvc.perform(post("/v1/numbers/en/convert")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.number").value(Long.MAX_VALUE));
    }

    /**
     * Test an invalid JSON payload returns a unified bad request error.
     *
     * @throws Exception the exception
     */
    @Test
    void testConvertNumberToWordsInvalidFormatReturnsBadRequest() throws Exception {
        mockMvc.perform(post("/v1/numbers/en/convert")
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
        EnglishWordsRequestDTO request = new EnglishWordsRequestDTO("two thousand five hundred");

        mockMvc.perform(post("/v1/numbers/en/to-number")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.number").value(2500));
    }

    /**
     * Test convert negative words to a number successfully.
     *
     * @throws Exception the exception
     */
    @Test
    void testConvertWordsToNumberNegativeSuccess() throws Exception {
        EnglishWordsRequestDTO request = new EnglishWordsRequestDTO("minus one hundred twenty-three");

        mockMvc.perform(post("/v1/numbers/en/to-number")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.number").value(-123));
    }

    /**
     * Test blank text is rejected by bean validation.
     *
     * @throws Exception the exception
     */
    @Test
    void testConvertWordsToNumberBlankReturnsValidationError() throws Exception {
        EnglishWordsRequestDTO request = new EnglishWordsRequestDTO("");

        mockMvc.perform(post("/v1/numbers/en/to-number")
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
        EnglishWordsRequestDTO request = new EnglishWordsRequestDTO("two thousand 500");

        mockMvc.perform(post("/v1/numbers/en/to-number")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("VALIDATION_ERROR"))
                .andExpect(jsonPath("$.message")
                        .value("The text may only contain English letters, single spaces and hyphens"));
    }

    /**
     * Test text exceeding the maximum length is rejected.
     *
     * @throws Exception the exception
     */
    @Test
    void testConvertWordsToNumberTooLongReturnsValidationError() throws Exception {
        EnglishWordsRequestDTO request = new EnglishWordsRequestDTO("a".repeat(301));

        mockMvc.perform(post("/v1/numbers/en/to-number")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("VALIDATION_ERROR"))
                .andExpect(jsonPath("$.message").value("The text must not exceed 300 characters"));
    }

    /**
     * Test a syntactically valid but unrecognized word is rejected by the service layer.
     *
     * @throws Exception the exception
     */
    @Test
    void testConvertWordsToNumberUnrecognizedWordReturnsBadRequest() throws Exception {
        EnglishWordsRequestDTO request = new EnglishWordsRequestDTO("banana");

        mockMvc.perform(post("/v1/numbers/en/to-number")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("ERROR"))
                .andExpect(jsonPath("$.message").value("Unrecognized word: 'banana'"));
    }
}
