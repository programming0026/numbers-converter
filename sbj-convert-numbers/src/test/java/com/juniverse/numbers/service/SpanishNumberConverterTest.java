package com.juniverse.numbers.service;

import com.juniverse.numbers.enums.Language;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Pruebas unitarias de {@link SpanishNumberConverter}.
 *
 * @author ajuar
 */
class SpanishNumberConverterTest {

    private SpanishNumberConverter converter;

    @BeforeEach
    void setUp() {
        converter = new SpanishNumberConverter();
    }

    /**
     * Idioma del convertidor.
     */
    @Test
    void testLanguage() {
        assertEquals(Language.ES, converter.language());
    }

    /**
     * Convertir cero.
     */
    @Test
    void testToWordsZero() {
        assertEquals("cero", converter.toWords(0));
    }

    /**
     * Convertir unidades, los números del 10 al 19 y el rango irregular del 20 al 29.
     */
    @Test
    void testToWordsUnitsTeensAndTwenties() {
        assertEquals("uno", converter.toWords(1));
        assertEquals("nueve", converter.toWords(9));
        assertEquals("diez", converter.toWords(10));
        assertEquals("once", converter.toWords(11));
        assertEquals("quince", converter.toWords(15));
        assertEquals("dieciséis", converter.toWords(16));
        assertEquals("diecinueve", converter.toWords(19));
        assertEquals("veinte", converter.toWords(20));
        assertEquals("veintiuno", converter.toWords(21));
        assertEquals("veintidós", converter.toWords(22));
        assertEquals("veintinueve", converter.toWords(29));
    }

    /**
     * Convertir decenas desde el 30 en adelante.
     */
    @Test
    void testToWordsTens() {
        assertEquals("treinta", converter.toWords(30));
        assertEquals("treinta y uno", converter.toWords(31));
        assertEquals("treinta y cinco", converter.toWords(35));
        assertEquals("noventa y nueve", converter.toWords(99));
    }

    /**
     * Convertir centenas, incluido el caso irregular «cien».
     */
    @Test
    void testToWordsHundreds() {
        assertEquals("cien", converter.toWords(100));
        assertEquals("ciento uno", converter.toWords(101));
        assertEquals("doscientos", converter.toWords(200));
        assertEquals("doscientos cincuenta", converter.toWords(250));
        assertEquals("novecientos noventa y nueve", converter.toWords(999));
    }

    /**
     * Convertir miles, incluida la apócope antes de «mil».
     */
    @Test
    void testToWordsThousands() {
        assertEquals("mil", converter.toWords(1000));
        assertEquals("dos mil quinientos", converter.toWords(2500));
        assertEquals("quince mil", converter.toWords(15000));
        assertEquals("veintiún mil", converter.toWords(21000));
        assertEquals("treinta y un mil", converter.toWords(31000));
    }

    /**
     * Convertir millones, incluida la apócope antes de «millones».
     */
    @Test
    void testToWordsMillions() {
        assertEquals("un millón", converter.toWords(1_000_000));
        assertEquals("cinco millones", converter.toWords(5_000_000));
        assertEquals("un millón quinientos mil", converter.toWords(1_500_000));
        assertEquals("veintiún millones", converter.toWords(21_000_000));
    }

    /**
     * Convertir billones (escala larga: billón = 10^12) y escalas mayores anidadas.
     */
    @Test
    void testToWordsBillions() {
        assertEquals("un billón", converter.toWords(1_000_000_000_000L));
        assertEquals("dos billones", converter.toWords(2_000_000_000_000L));
        assertEquals("mil billones", converter.toWords(1_000_000_000_000_000L));
    }

    /**
     * Convertir números negativos.
     */
    @Test
    void testToWordsNegative() {
        assertEquals("menos ciento veintitrés", converter.toWords(-123));
        assertEquals("menos un millón", converter.toWords(-1_000_000L));
    }

    /**
     * Se rechaza Long.MIN_VALUE en lugar de provocar un desbordamiento de pila.
     */
    @Test
    void testToWordsLongMinValueThrows() {
        assertThrows(IllegalArgumentException.class, () -> converter.toWords(Long.MIN_VALUE));
    }

    /**
     * Convertir palabras en números, incluso con acentos.
     */
    @Test
    void testToNumberBasic() {
        assertEquals(0L, converter.toNumber("cero"));
        assertEquals(19L, converter.toNumber("diecinueve"));
        assertEquals(123L, converter.toNumber("ciento veintitrés"));
        assertEquals(123L, converter.toNumber("ciento veintitres"));
        assertEquals(2500L, converter.toNumber("dos mil quinientos"));
        assertEquals(1_500_000L, converter.toNumber("un millón quinientos mil"));
        assertEquals(1_000_000_000_000_000L, converter.toNumber("mil billones"));
    }

    /**
     * Interpretar centenas en femenino («doscientas» en lugar de «doscientos»).
     */
    @Test
    void testToNumberFeminineHundreds() {
        assertEquals(200L, converter.toNumber("doscientas"));
    }

    /**
     * Interpretar palabras negativas.
     */
    @Test
    void testToNumberNegative() {
        assertEquals(-123L, converter.toNumber("menos ciento veintitrés"));
    }

    /**
     * La interpretación no distingue mayúsculas de minúsculas.
     */
    @Test
    void testToNumberIsCaseInsensitive() {
        assertEquals(123L, converter.toNumber("Ciento Veintitrés"));
    }

    /**
     * Se rechaza el texto en blanco.
     */
    @Test
    void testToNumberBlankThrows() {
        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
                () -> converter.toNumber("   "));
        assertEquals("La cantidad no puede estar vacía", ex.getMessage());
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
            0, 1, 5, 9, 10, 11, 15, 16, 19, 20, 21, 22, 29, 30, 35, 99, 100, 101, 200, 250,
            300, 999, 1000, 2000, 2500, 21000, 31000, 100000, 1_000_000, 1_500_000,
            5_000_000, 21_000_000, 1_000_000_000_000L, 2_000_000_000_000L,
            1_000_000_000_000_000L, -1, -100, -123, -1_000_000L
        };
        for (long sample : samples) {
            assertEquals(sample, converter.toNumber(converter.toWords(sample)),
                    "Round trip failed for " + sample);
        }
    }
}
