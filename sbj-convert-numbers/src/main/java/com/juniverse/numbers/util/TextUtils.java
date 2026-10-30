package com.juniverse.numbers.util;

import java.text.Normalizer;
import java.util.Locale;

public final class TextUtils {

    private TextUtils() {
    }

    /**
     * Minúsculas, sin acentos, sin signos (los guiones y comas pasan a espacio)
     * y con los espacios colapsados.
     */
    public static String normalize(String text) {
        if (text == null) {
            return "";
        }
        String withoutAccents = Normalizer
                .normalize(text.trim().toLowerCase(Locale.ROOT), Normalizer.Form.NFD)
                .replaceAll("\\p{InCombiningDiacriticalMarks}+", "");
        return withoutAccents
                .replaceAll("[^a-z\\s]", " ")
                .replaceAll("\\s+", " ")
                .trim();
    }

}
