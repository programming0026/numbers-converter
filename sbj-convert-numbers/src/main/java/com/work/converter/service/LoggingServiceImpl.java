package com.work.converter.service;

import com.work.converter.dto.RequestResponseDataDTO;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.StringUtils;

/**
 * The Class LoggingServiceImpl.
 * Implementation of LoggingService for handling request and response logging.
 *
 * @author ajuar
 */
@Slf4j
public class LoggingServiceImpl implements LoggingService {

    /**
     * Print log request and response.
     *
     * @param data the request response data
     */
    @Override
    public void printLogReqRes(RequestResponseDataDTO data) {
        printLogRequest(data);

        printLogResponse(data);
    }

    /**
     * Prints the log request.
     *
     * @param data the request response data
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
     * Prints the log response.
     *
     * @param data the request response data
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

    /**
     * Gets the url.
     *
     * @param req the req
     * @return the url
     */
    private String getUrl(RequestResponseDataDTO req) {
        return Stream.of(req.path(), req.query()).filter(StringUtils::hasText)
                .collect(Collectors.joining("?"));
    }

    /**
     * Checks if is not loggable URL.
     *
     * @param requestURI the request URI
     * @return true, if is not loggable URL
     */
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
