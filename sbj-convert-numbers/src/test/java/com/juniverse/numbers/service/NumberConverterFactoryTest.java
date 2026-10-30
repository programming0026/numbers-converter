package com.juniverse.numbers.service;

import com.juniverse.numbers.enums.Language;
import java.util.Locale;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;

/**
 * The Class NumberConverterFactoryTest.
 * Unit tests for {@link NumberConverterFactory}.
 *
 * @author ajuar
 */
class NumberConverterFactoryTest {

    /**
     * Test resolving the Spanish converter by language.
     */
    @Test
    void testOfLanguageSpanish() {
        NumberConverter converter = NumberConverterFactory.of(Language.ES);
        assertInstanceOf(SpanishNumberConverter.class, converter);
        assertEquals(Language.ES, converter.language());
    }

    /**
     * Test resolving the English converter by language.
     */
    @Test
    void testOfLanguageEnglish() {
        NumberConverter converter = NumberConverterFactory.of(Language.EN);
        assertInstanceOf(EnglishNumberConverter.class, converter);
        assertEquals(Language.EN, converter.language());
    }

    /**
     * Test the factory returns the same shared instance on every call.
     */
    @Test
    void testOfLanguageReturnsSameInstance() {
        assertSame(NumberConverterFactory.of(Language.ES), NumberConverterFactory.of(Language.ES));
        assertSame(NumberConverterFactory.of(Language.EN), NumberConverterFactory.of(Language.EN));
    }

    /**
     * Test a null language is rejected.
     */
    @Test
    void testOfNullLanguageThrows() {
        assertThrows(IllegalArgumentException.class, () -> NumberConverterFactory.of((Language) null));
    }

    /**
     * Test resolving a converter from a Locale.
     */
    @Test
    void testOfLocale() {
        assertInstanceOf(SpanishNumberConverter.class, NumberConverterFactory.of(Locale.forLanguageTag("es-MX")));
        assertInstanceOf(EnglishNumberConverter.class, NumberConverterFactory.of(Locale.forLanguageTag("en-US")));
    }

    /**
     * Test a null Locale is rejected.
     */
    @Test
    void testOfNullLocaleThrows() {
        assertThrows(IllegalArgumentException.class, () -> NumberConverterFactory.of((Locale) null));
    }

    /**
     * Test a Locale for an unsupported language is rejected.
     */
    @Test
    void testOfUnsupportedLocaleThrows() {
        assertThrows(IllegalArgumentException.class, () -> NumberConverterFactory.of(Locale.FRENCH));
    }

    /**
     * Test resolving a converter from a language tag.
     */
    @Test
    void testOfLanguageTag() {
        assertInstanceOf(SpanishNumberConverter.class, NumberConverterFactory.of("es"));
        assertInstanceOf(EnglishNumberConverter.class, NumberConverterFactory.of("en"));
    }
}
