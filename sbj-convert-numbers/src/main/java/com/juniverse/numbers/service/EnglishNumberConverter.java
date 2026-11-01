package com.juniverse.numbers.service;

import com.juniverse.numbers.enums.Language;
import com.juniverse.numbers.util.TextUtils;
import java.util.HashMap;
import java.util.Map;

public class EnglishNumberConverter implements NumberConverter {
    private static final String[] UP_TO_19 = {
            "zero", "one", "two", "three", "four", "five", "six", "seven", "eight", "nine",
            "ten", "eleven", "twelve", "thirteen", "fourteen", "fifteen", "sixteen",
            "seventeen", "eighteen", "nineteen"
    };

    private static final String[] TENS = {
            "", "", "twenty", "thirty", "forty", "fifty", "sixty", "seventy", "eighty", "ninety"
    };

    /** Escalas en orden descendente; deben mantenerse alineadas con SCALE_NAMES. */
    private static final long[] SCALE_VALUES = {
            1_000_000_000_000_000_000L, 1_000_000_000_000_000L, 1_000_000_000_000L,
            1_000_000_000L, 1_000_000L, 1_000L
    };

    private static final String[] SCALE_NAMES = {
            "quintillion", "quadrillion", "trillion", "billion", "million", "thousand"
    };

    private static final Map<String, Long> WORDS = new HashMap<>();
    private static final Map<String, Long> SCALES = new HashMap<>();

    static {
        for (int i = 0; i < UP_TO_19.length; i++) {
            WORDS.put(UP_TO_19[i], (long) i);
        }
        for (int ten = 2; ten < TENS.length; ten++) {
            WORDS.put(TENS[ten], (long) ten * 10);
        }
        for (int i = 0; i < SCALE_VALUES.length; i++) {
            SCALES.put(SCALE_NAMES[i], SCALE_VALUES[i]);
            SCALES.put(SCALE_NAMES[i] + "s", SCALE_VALUES[i]);
        }
    }

    @Override
    public Language language() {
        return Language.EN;
    }

    @Override
    public String toWords(long number) {
        if (number == Long.MIN_VALUE) {
            throw new IllegalArgumentException("Value out of range: " + number);
        }
        if (number < 0) {
            return "minus " + toWords(-number);
        }
        if (number < 1_000) {
            return belowThousand((int) number);
        }

        StringBuilder builder = new StringBuilder();
        long remainder = number;
        for (int i = 0; i < SCALE_VALUES.length; i++) {
            long scale = SCALE_VALUES[i];
            long count = remainder / scale;
            if (count > 0) {
                append(builder, toWords(count) + " " + SCALE_NAMES[i]);
                remainder %= scale;
            }
        }
        if (remainder > 0) {
            append(builder, belowThousand((int) remainder));
        }
        return builder.toString();
    }

    private String belowThousand(int value) {
        if (value < 100) {
            return belowHundred(value);
        }
        String hundreds = UP_TO_19[value / 100] + " hundred";
        int remainder = value % 100;
        return remainder == 0 ? hundreds : hundreds + " " + belowHundred(remainder);
    }

    private String belowHundred(int value) {
        if (value < 20) {
            return UP_TO_19[value];
        }
        int ten = value / 10;
        int unit = value % 10;
        return unit == 0 ? TENS[ten] : TENS[ten] + "-" + UP_TO_19[unit];
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
            throw new IllegalArgumentException("The amount cannot be empty");
        }

        boolean negative = normalized.startsWith("minus ") || normalized.startsWith("negative ");
        if (negative) {
            normalized = normalized.substring(normalized.indexOf(' ') + 1);
        }

        long total = 0;
        long current = 0;
        boolean hasValue = false;

        for (String token : normalized.split(" ")) {
            if (token.isEmpty() || token.equals("and") || token.equals("a")) {
                continue;
            }

            Long value = WORDS.get(token);
            if (value != null) {
                current += value;
                hasValue = true;
                continue;
            }

            if (token.equals("hundred") || token.equals("hundreds")) {
                current = (current == 0 ? 1 : current) * 100;
                hasValue = true;
                continue;
            }

            Long scale = SCALES.get(token);
            if (scale != null) {
                total += (current == 0 ? 1 : current) * scale;
                current = 0;
                hasValue = true;
                continue;
            }

            throw new IllegalArgumentException("Unrecognized word: '" + token + "'");
        }

        if (!hasValue) {
            throw new IllegalArgumentException("No valid amount was found in: " + text);
        }

        long result = total + current;
        return negative ? -result : result;
    }
}
