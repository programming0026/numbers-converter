package com.juniverse.numbers.util;

import java.text.Normalizer;
import java.util.Locale;

public final class TextUtils {

    private TextUtils() {
    }

    /**
     * Minúsculas, sin acentos, sin signos (los guiones y comas pasan a espacio)
     * y con los espacios colapsados.
     *
     * @param text texto de entrada (puede ser nulo)
     * @return el texto normalizado, o una cadena vacía si es nulo
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
