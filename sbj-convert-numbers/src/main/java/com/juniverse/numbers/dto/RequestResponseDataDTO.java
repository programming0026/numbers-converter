package com.juniverse.numbers.dto;

import java.util.Map;

/**
 * The Record RequestResponseDataDTO.
 * Data transfer object for request and response logging information.
 *
 * @author ajuar
 * @param method the HTTP method
 * @param path the request path
 * @param query the query string
 * @param requestHeaders the request headers
 * @param requestBody the request body
 * @param status the response status code
 * @param responseHeaders the response headers
 * @param responseBody the response body
 * @param remoteAddr the remote address
 */
public record RequestResponseDataDTO(
        String method,
        String path,
        String query,
        Map<String, String> requestHeaders,
        String requestBody,
        int status,
        Map<String, String> responseHeaders,
        String responseBody,
        String remoteAddr) {

}
