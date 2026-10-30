package com.juniverse.numbers.service;

import com.juniverse.numbers.enums.Language;
import java.util.EnumMap;
import java.util.Locale;
import java.util.Map;

public final class NumberConverterFactory {
    private static final Map<Language, NumberConverter> INSTANCES = new EnumMap<>(Language.class);

    static {
        INSTANCES.put(Language.ES, new SpanishNumberConverter());
        INSTANCES.put(Language.EN, new EnglishNumberConverter());
    }

    private NumberConverterFactory() {
    }

    /** Devuelve el conversor del idioma indicado. */
    public static NumberConverter of(Language language) {
        if (language == null) {
            throw new IllegalArgumentException("El idioma no puede ser null");
        }
        NumberConverter converter = INSTANCES.get(language);
        if (converter == null) {
            throw new IllegalArgumentException("Idioma sin implementación: " + language);
        }
        return converter;
    }

    /** Devuelve el conversor resolviendo el idioma desde un Locale. */
    public static NumberConverter of(Locale locale) {
        return of(Language.from(locale));
    }

    /** Devuelve el conversor a partir del código de idioma ("es", "en"). */
    public static NumberConverter of(String languageTag) {
        return of(Locale.forLanguageTag(languageTag));
    }

}
