package com.juniverse.numbers.service;

import com.juniverse.numbers.enums.Language;
import java.util.Locale;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;

/**
 * Pruebas unitarias de {@link NumberConverterFactory}.
 *
 * @author Java Universe
 */
class NumberConverterFactoryTest {

    /**
     * Obtener el convertidor en español por idioma.
     */
    @Test
    void testOfLanguageSpanish() {
        NumberConverter converter = NumberConverterFactory.of(Language.ES);
        assertInstanceOf(SpanishNumberConverter.class, converter);
        assertEquals(Language.ES, converter.language());
    }

    /**
     * Obtener el convertidor en inglés por idioma.
     */
    @Test
    void testOfLanguageEnglish() {
        NumberConverter converter = NumberConverterFactory.of(Language.EN);
        assertInstanceOf(EnglishNumberConverter.class, converter);
        assertEquals(Language.EN, converter.language());
    }

    /**
     * La fábrica devuelve la misma instancia compartida en cada llamada.
     */
    @Test
    void testOfLanguageReturnsSameInstance() {
        assertSame(NumberConverterFactory.of(Language.ES), NumberConverterFactory.of(Language.ES));
        assertSame(NumberConverterFactory.of(Language.EN), NumberConverterFactory.of(Language.EN));
    }

    /**
     * Se rechaza un idioma nulo.
     */
    @Test
    void testOfNullLanguageThrows() {
        assertThrows(IllegalArgumentException.class, () -> NumberConverterFactory.of((Language) null));
    }

    /**
     * Obtener un convertidor a partir de un Locale.
     */
    @Test
    void testOfLocale() {
        assertInstanceOf(SpanishNumberConverter.class, NumberConverterFactory.of(Locale.forLanguageTag("es-MX")));
        assertInstanceOf(EnglishNumberConverter.class, NumberConverterFactory.of(Locale.forLanguageTag("en-US")));
    }

    /**
     * Se rechaza un Locale nulo.
     */
    @Test
    void testOfNullLocaleThrows() {
        assertThrows(IllegalArgumentException.class, () -> NumberConverterFactory.of((Locale) null));
    }

    /**
     * Se rechaza un Locale de un idioma no soportado.
     */
    @Test
    void testOfUnsupportedLocaleThrows() {
        assertThrows(IllegalArgumentException.class, () -> NumberConverterFactory.of(Locale.FRENCH));
    }

    /**
     * Obtener un convertidor a partir de una etiqueta de idioma.
     */
    @Test
    void testOfLanguageTag() {
        assertInstanceOf(SpanishNumberConverter.class, NumberConverterFactory.of("es"));
        assertInstanceOf(EnglishNumberConverter.class, NumberConverterFactory.of("en"));
    }
}
