package com.juniverse.numbers.controller;

import tools.jackson.databind.ObjectMapper;
import com.juniverse.numbers.dto.SpanishNumberRequestDTO;
import com.juniverse.numbers.dto.SpanishWordsRequestDTO;
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
 * Pruebas de integración de {@link SpanishNumberController}.
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
     * Convertir número a palabras correctamente.
     *
     * @throws Exception si ocurre un error
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
     * Convertir un número negativo a palabras correctamente.
     *
     * @throws Exception si ocurre un error
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
     * La validación rechaza un número nulo.
     *
     * @throws Exception si ocurre un error
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
     * Se rechaza un número menor al límite inferior permitido.
     *
     * @throws Exception si ocurre un error
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
     * Se rechaza un número mayor al límite superior permitido.
     *
     * @throws Exception si ocurre un error
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
     * Se aceptan los valores límite.
     *
     * @throws Exception si ocurre un error
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
     * Un JSON inválido devuelve un error de petición incorrecta unificado.
     *
     * @throws Exception si ocurre un error
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
     * Convertir palabras a número correctamente.
     *
     * @throws Exception si ocurre un error
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
     * Convertir palabras con acentos a número correctamente.
     *
     * @throws Exception si ocurre un error
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
     * La validación rechaza el texto en blanco.
     *
     * @throws Exception si ocurre un error
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
     * Se rechaza el texto con caracteres fuera del patrón permitido.
     *
     * @throws Exception si ocurre un error
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
     * Se rechaza el texto que excede la longitud máxima.
     *
     * @throws Exception si ocurre un error
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
     * La capa de servicio rechaza una palabra válida pero no reconocida.
     *
     * @throws Exception si ocurre un error
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
