package com.juniverse.numbers.filter;

import com.juniverse.numbers.dto.RequestResponseDataDTO;
import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;
import java.util.AbstractMap;
import java.util.Collections;
import java.util.Map;
import java.util.stream.Collectors;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.web.util.ContentCachingRequestWrapper;
import org.springframework.web.util.ContentCachingResponseWrapper;
import com.juniverse.numbers.service.LoggingService;
import com.juniverse.numbers.util.Constants;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import jakarta.annotation.PreDestroy;
import org.slf4j.MDC;

/**
 * The Class LoggingFilter.
 *
 * @author ajuar
 */
@Component
@Slf4j
@Order(1)
public class LoggingFilter implements Filter {

    /**
     * Maximum number of pending log tasks allowed before new ones are discarded.
     */
    private static final int LOG_QUEUE_CAPACITY = 500;

    /**
     * The executor with a bounded thread pool and a bounded queue, sustained traffic burst
     * discards new log tasks instead of growing memory without limit.
     */
    private final ExecutorService executor = new ThreadPoolExecutor(
            10, 10, 0L, TimeUnit.MILLISECONDS,
            new ArrayBlockingQueue<>(LOG_QUEUE_CAPACITY),
            (task, exec) -> log.warn("Logging queue is full ({} pending); discarding a request/response log entry",
                    LOG_QUEUE_CAPACITY));

    /**
     * The loggingService.
     */
    private final LoggingService loggingService;

    /**
     * Instantiates a new logging filter.
     *
     * @param loggingService the logging service
     */
    public LoggingFilter(LoggingService loggingService) {
        this.loggingService = loggingService;
    }

    /**
     * Do filter.
     *
     * @param request the request
     * @param response the response
     * @param chain the chain
     * @throws IOException the IO exception
     * @throws ServletException the servlet exception
     */
    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {

        ContentCachingRequestWrapper requestWrapper
                = new ContentCachingRequestWrapper((HttpServletRequest) request);
        ContentCachingResponseWrapper responseWrapper
                = new ContentCachingResponseWrapper((HttpServletResponse) response);

        try {
            chain.doFilter(requestWrapper, responseWrapper);
        } finally {
            try {
                RequestResponseDataDTO data = createData(requestWrapper, responseWrapper);
                printLog(data);
            } catch (Exception ex) {
                log.error("Error creating or printing log data", ex);
            } finally {
                responseWrapper.copyBodyToResponse();
            }
        }
    }

    /**
     * Print log asynchronously.
     *
     * @param data the request response data
     */
    private void printLog(RequestResponseDataDTO data) {

        String transactionId = data.responseHeaders().get(Constants.HEADER_TRANSACTION_ID_KEY);
        if (transactionId != null) {
            MDC.put(Constants.TRACE_TX, transactionId);
        }

        Map<String, String> contextMap = MDC.getCopyOfContextMap();

        if (transactionId != null) {
            MDC.remove(Constants.TRACE_TX);
        }

        executor.execute(() -> {
            if (contextMap != null) {
                MDC.setContextMap(contextMap);
            }
            try {
                this.loggingService.printLogReqRes(data);
            } finally {
                MDC.clear();
            }
        });
    }

    /**
     * Shutdown executor when component is destroyed.
     */
    @PreDestroy
    public void shutdown() {
        executor.shutdown();
    }

    /**
     * Create data DTO from request and response wrappers.
     *
     * @param requestWrapper the request wrapper
     * @param responseWrapper the response wrapper
     * @return the request response data DTO
     * @throws UnsupportedEncodingException the unsupported encoding exception
     */
    private RequestResponseDataDTO createData(ContentCachingRequestWrapper requestWrapper,
            ContentCachingResponseWrapper responseWrapper) throws UnsupportedEncodingException {

        requestWrapper.getCharacterEncoding();
        String reqEncoding = requestWrapper.getCharacterEncoding();
        String resEncoding = responseWrapper.getCharacterEncoding() != null
                ? responseWrapper.getCharacterEncoding()
                : StandardCharsets.UTF_8.name();

        return new RequestResponseDataDTO(
                requestWrapper.getMethod(),
                requestWrapper.getRequestURI(),
                requestWrapper.getQueryString(),
                getHeaders(requestWrapper),
                new String(requestWrapper.getContentAsByteArray(), reqEncoding),
                responseWrapper.getStatus(),
                getHeaders(responseWrapper),
                new String(responseWrapper.getContentAsByteArray(), resEncoding),
                requestWrapper.getRemoteAddr()
        );
    }

    /**
     * Gets the headers.
     *
     * @param res the res
     * @return the headers
     */
    private Map<String, String> getHeaders(ContentCachingResponseWrapper res) {
        return res.getHeaderNames().stream()
                .distinct()
                .map(name -> new AbstractMap.SimpleImmutableEntry<>(name, res.getHeader(name)))
                .filter(entry -> entry.getValue() != null)
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue, (v1, v2) -> v1));
    }

    /**
     * Gets the headers.
     *
     * @param req the req
     * @return the headers
     */
    private Map<String, String> getHeaders(ContentCachingRequestWrapper req) {
        return Collections.list(req.getHeaderNames()).stream()
                .distinct()
                .map(name -> new AbstractMap.SimpleImmutableEntry<>(name, req.getHeader(name)))
                .filter(entry -> entry.getValue() != null)
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue, (v1, v2) -> v1));
    }

}
