package com.juniverse.numbers.service;

import com.juniverse.numbers.enums.Language;
/**
 * Conversión bidireccional entre cantidades numéricas y su representación en letras.
 * Cada implementación cubre un idioma y debe ser inmutable / thread-safe.
 */
public interface NumberConverter {
    /** Idioma que atiende esta implementación. */
    Language language();

    /**
     * Convierte un número a letras.
     *
     * @param number valor a convertir (admite negativos)
     * @return la cantidad escrita en letras
     */
    String toWords(long number);

    /**
     * Convierte una cantidad escrita en letras a su valor numérico.
     *
     * @param text cantidad en letras, p. ej. "dos mil quinientos" / "two thousand five hundred"
     * @return el valor numérico
     * @throws IllegalArgumentException si el texto está vacío o contiene palabras no reconocidas
     */
    long toNumber(String text);

}
