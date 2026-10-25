package com.work.converter.util;

import java.util.Map;

/**
 * The Class Constants.
 *
 * @author linux
 */
public final class Constants {

    /**
     * Instantiates a new constants.
     */
    private Constants() {

    }

    /**
     * Arrays estáticos para evitar recreación en cada llamada.
     */
    public static final String[] UNITS = {
        "", "uno", "dos", "tres", "cuatro", "cinco", "seis", "siete", "ocho", "nueve"
    };

    /**
     * Decenas.
     */
    public static final String[] TENS = {
        "", "", "veinte", "treinta", "cuarenta", "cincuenta",
        "sesenta", "setenta", "ochenta", "noventa"
    };

    /**
     * Especiales.
     */
    public static final String[] SPECIALS = {
        "", "once", "doce", "trece", "catorce", "quince",
        "dieciseis", "diecisiete", "dieciocho", "diecinueve"
    };

    /**
     * Centenas.
     */
    public static final String[] HUNDREDS = {
        "", "ciento", "doscientos", "trescientos", "cuatrocientos", "quinientos",
        "seiscientos", "setecientos", "ochocientos", "novecientos"
    };

    /**
     * Map para escalas grandes.
     */
    public static final Map<Integer, String[]> SCALES = Map.of(
            3, new String[]{"mil", "mil"},
            6, new String[]{"millon", "millones"},
            9, new String[]{"mil millones", "mil millones"},
            12, new String[]{"billon", "billones"}
    );

    /**
     * Email Swagger.
     */
    public static final String SWAGGER_EMAIL = "programacion0025@gmail.com";

    /**
     * Descripcion Swagger.
     */
    public static final String SWAGGER_DESCRIPTION = "Application to convert numbers to words";

    /**
     * Nombre Swagger.
     */
    public static final String SWAGGER_NAME = "AJO";

    /**
     * Url Swagger.
     */
    public static final String SWAGGER_URL = "https://github.com/AJuarez0021";

    /**
     * The Constant HEADER_TRANSACTION_ID_KEY.
     */
    public static final String HEADER_TRANSACTION_ID_KEY = "transaction-id";

    /**
     * The Constant TRACE_TX.
     */
    public static final String TRACE_TX = "TRACE_TX";
}
