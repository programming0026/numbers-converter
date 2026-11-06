package com.juniverse.numbers.service;

import com.juniverse.numbers.dto.RequestResponseDataDTO;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.StringUtils;

/**
 * Implementación de LoggingService para registrar peticiones y respuestas.
 *
 * @author Java Universe
 */
@Slf4j
public class LoggingServiceImpl implements LoggingService {

    /**
     * Imprime el registro de la petición y la respuesta.
     *
     * @param data datos de respuesta de petición
     */
    @Override
    public void printLogReqRes(RequestResponseDataDTO data) {
        printLogRequest(data);

        printLogResponse(data);
    }

    /**
     * Imprime el registro de la petición.
     *
     * @param data datos de respuesta de petición
     */
    private void printLogRequest(RequestResponseDataDTO data) {
        if (!isNotLoggableURL(data.path())) {
            try {
                String requestURL = getUrl(data);
                Map<String, String> headers = data.requestHeaders();
                String request = data.requestBody();
                log.info("Request.RemoteAddr: {}", data.remoteAddr());
                log.info("Request.Method: {}", data.method());
                log.info("Request.URL: {}", requestURL);
                log.info("Request.Headers: {}", headers);
                log.info("Request.body: {}", request);
            } catch (Exception ex) {
                log.error("Error: ", ex);
            }
        }
    }

    /**
     * Imprime el registro de la respuesta.
     *
     * @param data datos de respuesta de petición
     */
    private void printLogResponse(RequestResponseDataDTO data) {
        if (!isNotLoggableURL(data.path())) {
            try {
                Map<String, String> headers = data.responseHeaders();
                String response = data.responseBody();
                log.info("Response.Status: {}", data.status());
                log.info("Response.Headers: {}", headers);
                log.info("Response.body: {}", response);
                
            } catch (Exception ex) {
                log.error("Error: ", ex);
            }
        }
    }

    private String getUrl(RequestResponseDataDTO req) {
        return Stream.of(req.path(), req.query()).filter(StringUtils::hasText)
                .collect(Collectors.joining("?"));
    }

    private boolean isNotLoggableURL(final String requestURI) {
        if (!StringUtils.hasText(requestURI)) {
            return false;
        }
        if (requestURI.startsWith("/docs/")) {
            return true;
        }
        if (requestURI.contains("swagger")) {
            return true;
        }
        if (requestURI.endsWith("docs")) {
            return true;
        }
        return requestURI.contains("management");
    }
}
