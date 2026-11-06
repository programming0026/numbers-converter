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
 * @author Java Universe
 */
@Component
@Slf4j
@Order(1)
public class LoggingFilter implements Filter {

    /**
     * Cantidad máxima de tareas de registro pendientes antes de descartar las nuevas.
     */
    private static final int LOG_QUEUE_CAPACITY = 500;

    /** Bytes máximos del cuerpo de la petición que se guardan en memoria para el registro (Spring 7 exige límite). */
    private static final int MAX_PAYLOAD_BYTES = 64 * 1024;

    /**
     * Ejecutor con un grupo de hilos y una cola acotados: ante una ráfaga sostenida de tráfico
     * descarta tareas de registro nuevas en lugar de crecer la memoria sin límite.
     */
    private final ExecutorService executor = new ThreadPoolExecutor(
            10, 10, 0L, TimeUnit.MILLISECONDS,
            new ArrayBlockingQueue<>(LOG_QUEUE_CAPACITY),
            (task, exec) -> log.warn("Logging queue is full ({} pending); discarding a request/response log entry",
                    LOG_QUEUE_CAPACITY));

    /**
     * Servicio de registro.
     */
    private final LoggingService loggingService;

    public LoggingFilter(LoggingService loggingService) {
        this.loggingService = loggingService;
    }

    /**
     * Filtra la petición, registra la entrada y la salida.
     *
     * @param request petición
     * @param response respuesta
     * @param chain cadena de filtros
     * @throws IOException si ocurre un error de entrada/salida
     * @throws ServletException si falla el procesamiento del servlet
     */
    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {

        ContentCachingRequestWrapper requestWrapper
                = new ContentCachingRequestWrapper((HttpServletRequest) request, MAX_PAYLOAD_BYTES);
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
     * Imprime el registro de forma asíncrona.
     *
     * @param data datos de respuesta de petición
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
     * Detiene el ejecutor al destruirse el componente.
     */
    @PreDestroy
    public void shutdown() {
        executor.shutdown();
    }

    /**
     * Crea el DTO con los datos de los envoltorios de petición y respuesta.
     *
     * @param requestWrapper envoltorio de la petición
     * @param responseWrapper envoltorio de la respuesta
     * @return DTO de datos de respuesta de petición
     * @throws UnsupportedEncodingException si la codificación no es soportada
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

    private Map<String, String> getHeaders(ContentCachingResponseWrapper res) {
        return res.getHeaderNames().stream()
                .distinct()
                .map(name -> new AbstractMap.SimpleImmutableEntry<>(name, res.getHeader(name)))
                .filter(entry -> entry.getValue() != null)
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue, (v1, v2) -> v1));
    }

    private Map<String, String> getHeaders(ContentCachingRequestWrapper req) {
        return Collections.list(req.getHeaderNames()).stream()
                .distinct()
                .map(name -> new AbstractMap.SimpleImmutableEntry<>(name, req.getHeader(name)))
                .filter(entry -> entry.getValue() != null)
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue, (v1, v2) -> v1));
    }

}
