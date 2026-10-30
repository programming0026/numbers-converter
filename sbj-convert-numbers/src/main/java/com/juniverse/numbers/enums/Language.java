package com.juniverse.numbers.enums;

import java.util.Locale;

public enum Language {
    /** Español de Latinoamérica (escala larga: billón = 10^12). */
    ES("es"),

    /** Inglés (escala corta: billion = 10^9). */
    EN("en");

    private final String code;

    Language(String code) {
        this.code = code;
    }

    public String code() {
        return code;
    }

    /** Resuelve el idioma a partir de un Locale */
    public static Language from(Locale locale) {
        if (locale == null) {
            throw new IllegalArgumentException("El locale no puede ser null");
        }
        String tag = locale.getLanguage().toLowerCase(Locale.ROOT);
        for (Language language : values()) {
            if (language.code.equals(tag)) {
                return language;
            }
        }
        throw new IllegalArgumentException("Idioma no soportado: " + locale.toLanguageTag());
    }

}
