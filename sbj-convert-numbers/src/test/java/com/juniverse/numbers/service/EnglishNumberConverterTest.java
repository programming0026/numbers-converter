package com.juniverse.numbers.service;

import com.juniverse.numbers.enums.Language;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Pruebas unitarias de {@link EnglishNumberConverter}.
 *
 * @author ajuar
 */
class EnglishNumberConverterTest {

    private EnglishNumberConverter converter;

    @BeforeEach
    void setUp() {
        converter = new EnglishNumberConverter();
    }

    /**
     * Idioma del convertidor.
     */
    @Test
    void testLanguage() {
        assertEquals(Language.EN, converter.language());
    }

    /**
     * Convertir cero.
     */
    @Test
    void testToWordsZero() {
        assertEquals("zero", converter.toWords(0));
    }

    /**
     * Convertir unidades y los números del 10 al 19.
     */
    @Test
    void testToWordsUnitsAndTeens() {
        assertEquals("one", converter.toWords(1));
        assertEquals("nine", converter.toWords(9));
        assertEquals("ten", converter.toWords(10));
        assertEquals("eleven", converter.toWords(11));
        assertEquals("fifteen", converter.toWords(15));
        assertEquals("nineteen", converter.toWords(19));
    }

    /**
     * Convertir decenas.
     */
    @Test
    void testToWordsTens() {
        assertEquals("twenty", converter.toWords(20));
        assertEquals("twenty-one", converter.toWords(21));
        assertEquals("thirty-five", converter.toWords(35));
        assertEquals("ninety-nine", converter.toWords(99));
    }

    /**
     * Convertir centenas.
     */
    @Test
    void testToWordsHundreds() {
        assertEquals("one hundred", converter.toWords(100));
        assertEquals("one hundred one", converter.toWords(101));
        assertEquals("two hundred fifty", converter.toWords(250));
        assertEquals("nine hundred ninety-nine", converter.toWords(999));
    }

    /**
     * Convertir miles.
     */
    @Test
    void testToWordsThousands() {
        assertEquals("one thousand", converter.toWords(1000));
        assertEquals("two thousand five hundred", converter.toWords(2500));
        assertEquals("twenty-one thousand", converter.toWords(21000));
    }

    /**
     * Convertir millones.
     */
    @Test
    void testToWordsMillions() {
        assertEquals("one million", converter.toWords(1000000));
        assertEquals("one million five hundred thousand", converter.toWords(1500000));
        assertEquals("twenty-one million", converter.toWords(21000000));
    }

    /**
     * Convertir billones, trillones, cuatrillones y quintillones (escala corta).
     */
    @Test
    void testToWordsLargeScales() {
        assertEquals("one billion", converter.toWords(1_000_000_000L));
        assertEquals("one trillion", converter.toWords(1_000_000_000_000L));
        assertEquals("one quadrillion", converter.toWords(1_000_000_000_000_000L));
        assertEquals("one quintillion", converter.toWords(1_000_000_000_000_000_000L));
    }

    /**
     * Convertir números negativos.
     */
    @Test
    void testToWordsNegative() {
        assertEquals("minus one hundred twenty-three", converter.toWords(-123));
        assertEquals("minus one million", converter.toWords(-1_000_000L));
    }

    /**
     * Se rechaza Long.MIN_VALUE en lugar de provocar un desbordamiento de pila.
     */
    @Test
    void testToWordsLongMinValueThrows() {
        assertThrows(IllegalArgumentException.class, () -> converter.toWords(Long.MIN_VALUE));
    }

    /**
     * Convertir palabras de nuevo en números.
     */
    @Test
    void testToNumberBasic() {
        assertEquals(0L, converter.toNumber("zero"));
        assertEquals(19L, converter.toNumber("nineteen"));
        assertEquals(123L, converter.toNumber("one hundred twenty-three"));
        assertEquals(2500L, converter.toNumber("two thousand five hundred"));
        assertEquals(1500000L, converter.toNumber("one million five hundred thousand"));
    }

    /**
     * Interpretar palabras negativas.
     */
    @Test
    void testToNumberNegative() {
        assertEquals(-123L, converter.toNumber("minus one hundred twenty-three"));
        assertEquals(-1L, converter.toNumber("negative one"));
    }

    /**
     * La interpretación no distingue mayúsculas de minúsculas.
     */
    @Test
    void testToNumberIsCaseInsensitive() {
        assertEquals(123L, converter.toNumber("One Hundred Twenty-Three"));
    }

    /**
     * Se rechaza el texto en blanco.
     */
    @Test
    void testToNumberBlankThrows() {
        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
                () -> converter.toNumber("   "));
        assertEquals("The amount cannot be empty", ex.getMessage());
    }

    /**
     * Se rechazan las palabras no reconocidas.
     */
    @Test
    void testToNumberUnrecognizedWordThrows() {
        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
                () -> converter.toNumber("banana"));
        assertTrue(ex.getMessage().contains("banana"));
    }

    /**
     * Convertir a palabras y luego a número devuelve el valor original.
     */
    @Test
    void testRoundTrip() {
        long[] samples = {
            0, 1, 7, 10, 11, 15, 19, 20, 21, 35, 99, 100, 101, 250, 999, 1000, 2500, 15000,
            21000, 31000, 1_000_000, 1_500_000, 5_000_000, 21_000_000, 1_000_000_000L,
            1_000_000_000_000L, 1_000_000_000_000_000L, 1_000_000_000_000_000_000L,
            -1, -100, -123, -1_000_000L
        };
        for (long sample : samples) {
            assertEquals(sample, converter.toNumber(converter.toWords(sample)),
                    "Round trip failed for " + sample);
        }
    }
}
