package com.juniverse.numbers.controller;

import tools.jackson.databind.ObjectMapper;
import com.juniverse.numbers.dto.EnglishNumberRequestDTO;
import com.juniverse.numbers.dto.EnglishWordsRequestDTO;
import com.juniverse.numbers.util.Constants;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Pruebas de integración de {@link EnglishNumberController}.
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
     * Convertir número a palabras correctamente.
     *
     * @throws Exception si ocurre un error
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
     * Convertir un número negativo a palabras correctamente.
     *
     * @throws Exception si ocurre un error
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
     * La validación rechaza un número nulo.
     *
     * @throws Exception si ocurre un error
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
     * Se rechaza un número menor al límite inferior permitido.
     *
     * @throws Exception si ocurre un error
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
     * Se acepta Long.MAX_VALUE, el límite superior.
     *
     * @throws Exception si ocurre un error
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
     * Un JSON inválido devuelve un error de petición incorrecta unificado.
     *
     * @throws Exception si ocurre un error
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
     * Convertir palabras a número correctamente.
     *
     * @throws Exception si ocurre un error
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
     * Convertir palabras negativas a número correctamente.
     *
     * @throws Exception si ocurre un error
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
     * La validación rechaza el texto en blanco.
     *
     * @throws Exception si ocurre un error
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
     * Se rechaza el texto con caracteres fuera del patrón permitido.
     *
     * @throws Exception si ocurre un error
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
     * Se rechaza el texto que excede la longitud máxima.
     *
     * @throws Exception si ocurre un error
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
     * La capa de servicio rechaza una palabra válida pero no reconocida.
     *
     * @throws Exception si ocurre un error
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
