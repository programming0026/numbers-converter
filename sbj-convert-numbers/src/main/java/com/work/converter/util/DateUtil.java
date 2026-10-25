package com.work.converter.util;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * The Class DateUtil.
 *
 * @author ajuar
 */
public final class DateUtil {

    /** The Constant PATTERN_DATE. */
    public static final String PATTERN_DATE = "yyyy-MM-dd HH:mm:ss";

    /** The Constant TIME_ZONE. */
    public static final String TIME_ZONE = "America/Mexico_City";

    /** The Constant ZONE_ID. */
    public static final java.time.ZoneId ZONE_ID = java.time.ZoneId.of(TIME_ZONE);

    /** The Constant DEFAULT_FORMATTER. */
    public static final DateTimeFormatter DEFAULT_FORMATTER = DateTimeFormatter.ofPattern(PATTERN_DATE);

    /**
     * Instantiates a new date util.
     */
    private DateUtil() {

    }

    /**
     * Gets the formatted date time for current time in configured timezone.
     *
     * @return the formatted date string
     */
    public static String getCurrentFormattedDate() {
        return LocalDateTime.now(ZONE_ID).format(DEFAULT_FORMATTER);
    }

    /**
     * Gets the format.
     *
     * @param dateTime the date time
     * @return the format
     */
    public static String getFormat(LocalDateTime dateTime) {
        return dateTime.format(DEFAULT_FORMATTER);
    }

    /**
     * Gets the format.
     *
     * @param pattern the pattern
     * @param dateTime the date time
     * @return the format
     */
    public static String getFormat(String pattern, LocalDateTime dateTime) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern(pattern);
        return dateTime.format(formatter);
    }
}
