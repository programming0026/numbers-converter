package com.work.converter.service;

import com.work.converter.enums.Language;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * The Class EnglishNumberConverterTest.
 * Unit tests for {@link EnglishNumberConverter}.
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
     * Test language.
     */
    @Test
    void testLanguage() {
        assertEquals(Language.EN, converter.language());
    }

    /**
     * Test convert zero.
     */
    @Test
    void testToWordsZero() {
        assertEquals("zero", converter.toWords(0));
    }

    /**
     * Test convert units and teens.
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
     * Test convert tens.
     */
    @Test
    void testToWordsTens() {
        assertEquals("twenty", converter.toWords(20));
        assertEquals("twenty-one", converter.toWords(21));
        assertEquals("thirty-five", converter.toWords(35));
        assertEquals("ninety-nine", converter.toWords(99));
    }

    /**
     * Test convert hundreds.
     */
    @Test
    void testToWordsHundreds() {
        assertEquals("one hundred", converter.toWords(100));
        assertEquals("one hundred one", converter.toWords(101));
        assertEquals("two hundred fifty", converter.toWords(250));
        assertEquals("nine hundred ninety-nine", converter.toWords(999));
    }

    /**
     * Test convert thousands.
     */
    @Test
    void testToWordsThousands() {
        assertEquals("one thousand", converter.toWords(1000));
        assertEquals("two thousand five hundred", converter.toWords(2500));
        assertEquals("twenty-one thousand", converter.toWords(21000));
    }

    /**
     * Test convert millions.
     */
    @Test
    void testToWordsMillions() {
        assertEquals("one million", converter.toWords(1000000));
        assertEquals("one million five hundred thousand", converter.toWords(1500000));
        assertEquals("twenty-one million", converter.toWords(21000000));
    }

    /**
     * Test convert billions, trillions, quadrillions and quintillions (short scale).
     */
    @Test
    void testToWordsLargeScales() {
        assertEquals("one billion", converter.toWords(1_000_000_000L));
        assertEquals("one trillion", converter.toWords(1_000_000_000_000L));
        assertEquals("one quadrillion", converter.toWords(1_000_000_000_000_000L));
        assertEquals("one quintillion", converter.toWords(1_000_000_000_000_000_000L));
    }

    /**
     * Test convert negative numbers.
     */
    @Test
    void testToWordsNegative() {
        assertEquals("minus one hundred twenty-three", converter.toWords(-123));
        assertEquals("minus one million", converter.toWords(-1_000_000L));
    }

    /**
     * Test Long.MIN_VALUE is rejected instead of causing a stack overflow.
     */
    @Test
    void testToWordsLongMinValueThrows() {
        assertThrows(IllegalArgumentException.class, () -> converter.toWords(Long.MIN_VALUE));
    }

    /**
     * Test parsing words back into numbers.
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
     * Test parsing negative words.
     */
    @Test
    void testToNumberNegative() {
        assertEquals(-123L, converter.toNumber("minus one hundred twenty-three"));
        assertEquals(-1L, converter.toNumber("negative one"));
    }

    /**
     * Test parsing is case-insensitive.
     */
    @Test
    void testToNumberIsCaseInsensitive() {
        assertEquals(123L, converter.toNumber("One Hundred Twenty-Three"));
    }

    /**
     * Test blank text is rejected.
     */
    @Test
    void testToNumberBlankThrows() {
        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
                () -> converter.toNumber("   "));
        assertEquals("The amount cannot be empty", ex.getMessage());
    }

    /**
     * Test unrecognized words are rejected.
     */
    @Test
    void testToNumberUnrecognizedWordThrows() {
        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
                () -> converter.toNumber("banana"));
        assertTrue(ex.getMessage().contains("banana"));
    }

    /**
     * Test toWords followed by toNumber returns the original value.
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
