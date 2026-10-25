package com.work.converter.service;

import com.work.converter.enums.Language;
import com.work.converter.util.TextUtils;
import java.util.HashMap;
import java.util.Map;

public class SpanishNumberConverter implements NumberConverter {
    private static final long THOUSAND = 1_000L;
    private static final long MILLION = 1_000_000L;
    private static final long BILLION = 1_000_000_000_000L;

    /** Formas irregulares del 0 al 29, que en español son una sola palabra. */
    private static final String[] UP_TO_29 = {
            "cero", "uno", "dos", "tres", "cuatro", "cinco", "seis", "siete", "ocho", "nueve",
            "diez", "once", "doce", "trece", "catorce", "quince", "dieciséis", "diecisiete",
            "dieciocho", "diecinueve", "veinte", "veintiuno", "veintidós", "veintitrés",
            "veinticuatro", "veinticinco", "veintiséis", "veintisiete", "veintiocho", "veintinueve"
    };

    private static final String[] TENS = {
            "", "", "veinte", "treinta", "cuarenta", "cincuenta", "sesenta", "setenta", "ochenta", "noventa"
    };

    private static final String[] HUNDREDS = {
            "", "ciento", "doscientos", "trescientos", "cuatrocientos", "quinientos",
            "seiscientos", "setecientos", "ochocientos", "novecientos"
    };

    /** Palabra -> valor. Las claves van normalizadas (minúsculas y sin acentos). */
    private static final Map<String, Long> WORDS = new HashMap<>();

    static {
        for (int i = 0; i < UP_TO_29.length; i++) {
            WORDS.put(TextUtils.normalize(UP_TO_29[i]), (long) i);
        }

        WORDS.put("un", 1L);
        WORDS.put("una", 1L);
        WORDS.put("veintiun", 21L);
        WORDS.put("veintiuna", 21L);
        for (int ten = 3; ten < TENS.length; ten++) {
            WORDS.put(TENS[ten], (long) ten * 10);
        }
        WORDS.put("cien", 100L);
        for (int hundred = 1; hundred < HUNDREDS.length; hundred++) {
            WORDS.put(HUNDREDS[hundred], (long) hundred * 100);
            if (hundred > 1) {
                WORDS.put(HUNDREDS[hundred].replace("cientos", "cientas"), (long) hundred * 100);
            }
        }
    }

    @Override
    public Language language() {
        return Language.ES;
    }

    @Override
    public String toWords(long number) {
        if (number == Long.MIN_VALUE) {
            throw new IllegalArgumentException("Valor fuera de rango: " + number);
        }
        if (number < 0) {
            return "menos " + toWords(-number);
        }
        if (number < THOUSAND) {
            return belowThousand((int) number);
        }

        StringBuilder builder = new StringBuilder();
        long remainder = number;

        long billions = remainder / BILLION;
        remainder %= BILLION;
        long millions = remainder / MILLION;
        remainder %= MILLION;
        long thousands = remainder / THOUSAND;
        remainder %= THOUSAND;

        if (billions > 0) {
            append(builder, billions == 1 ? "un billón" : apocopate(toWords(billions)) + " billones");
        }
        if (millions > 0) {
            append(builder, millions == 1 ? "un millón" : apocopate(toWords(millions)) + " millones");
        }
        if (thousands > 0) {
            append(builder, thousands == 1 ? "mil" : apocopate(toWords(thousands)) + " mil");
        }
        if (remainder > 0) {
            append(builder, belowThousand((int) remainder));
        }
        return builder.toString();
    }

    private String belowThousand(int value) {
        if (value == 100) {
            return "cien";
        }
        if (value < 100) {
            return belowHundred(value);
        }
        String hundreds = HUNDREDS[value / 100];
        int remainder = value % 100;
        return remainder == 0 ? hundreds : hundreds + " " + belowHundred(remainder);
    }

    private String belowHundred(int value) {
        if (value < 30) {
            return UP_TO_29[value];
        }
        int ten = value / 10;
        int unit = value % 10;
        return unit == 0 ? TENS[ten] : TENS[ten] + " y " + UP_TO_29[unit];
    }

    /** Apócope ante un multiplicador: "veintiuno mil" -> "veintiún mil", "treinta y uno" -> "treinta y un". */
    private String apocopate(String words) {
        if (words.endsWith("veintiuno")) {
            return words.substring(0, words.length() - "veintiuno".length()) + "veintiún";
        }
        if (words.endsWith("uno")) {
            return words.substring(0, words.length() - "uno".length()) + "un";
        }
        return words;
    }

    private void append(StringBuilder builder, String part) {
        if (!builder.isEmpty()) {
            builder.append(' ');
        }
        builder.append(part);
    }

    @Override
    public long toNumber(String text) {
        String normalized = TextUtils.normalize(text);
        if (normalized.isEmpty()) {
            throw new IllegalArgumentException("La cantidad no puede estar vacía");
        }

        boolean negative = normalized.startsWith("menos ");
        if (negative) {
            normalized = normalized.substring("menos ".length());
        }

        long total = 0;
        long thousands = 0;
        long current = 0;
        boolean hasValue = false;

        for (String token : normalized.split(" ")) {
            if (token.isEmpty() || token.equals("y") || token.equals("de") || token.equals("con")) {
                continue;
            }

            Long value = WORDS.get(token);
            if (value != null) {
                current += value;
                hasValue = true;
                continue;
            }

            switch (token) {
                case "mil":
                case "miles":
                    current = (current == 0 ? 1 : current) * THOUSAND;
                    thousands += current;
                    current = 0;
                    hasValue = true;
                    break;
                case "millon":
                case "millones":
                    long millionBlock = thousands + current;
                    total += (millionBlock == 0 ? 1 : millionBlock) * MILLION;
                    thousands = 0;
                    current = 0;
                    hasValue = true;
                    break;
                case "billon":
                case "billones":
                    long billionBlock = total + thousands + current;
                    total = (billionBlock == 0 ? 1 : billionBlock) * BILLION;
                    thousands = 0;
                    current = 0;
                    hasValue = true;
                    break;
                default:
                    throw new IllegalArgumentException("Palabra no reconocida: '" + token + "'");
            }
        }

        if (!hasValue) {
            throw new IllegalArgumentException("No se encontró ninguna cantidad válida en: " + text);
        }

        long result = total + thousands + current;
        return negative ? -result : result;
    }

}
