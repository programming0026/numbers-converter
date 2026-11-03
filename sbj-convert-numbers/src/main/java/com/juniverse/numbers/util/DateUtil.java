package com.juniverse.numbers.util;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * @author ajuar
 */
public final class DateUtil {

    /** Formato de fecha y hora de las respuestas y los registros. */
    public static final String PATTERN_DATE = "yyyy-MM-dd HH:mm:ss";

    /** Zona horaria con la que se formatean las fechas. */
    public static final String TIME_ZONE = "America/Mexico_City";

    /** Zona horaria de {@link #TIME_ZONE}. */
    public static final java.time.ZoneId ZONE_ID = java.time.ZoneId.of(TIME_ZONE);

    /** Formateador con el patrón {@link #PATTERN_DATE}. */
    public static final DateTimeFormatter DEFAULT_FORMATTER = DateTimeFormatter.ofPattern(PATTERN_DATE);

    private DateUtil() {

    }

    /**
     * Obtiene la fecha y hora actual con formato, en la zona horaria configurada.
     *
     * @return fecha con formato
     */
    public static String getCurrentFormattedDate() {
        return LocalDateTime.now(ZONE_ID).format(DEFAULT_FORMATTER);
    }

    /**
     * Obtiene formato.
     *
     * @param dateTime tiempo de fecha
     * @return formato
     */
    public static String getFormat(LocalDateTime dateTime) {
        return dateTime.format(DEFAULT_FORMATTER);
    }

    /**
     * Obtiene formato.
     *
     * @param pattern patrón de formato
     * @param dateTime tiempo de fecha
     * @return formato
     */
    public static String getFormat(String pattern, LocalDateTime dateTime) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern(pattern);
        return dateTime.format(formatter);
    }
}
