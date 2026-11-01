package com.juniverse.numbers.filter;

import com.juniverse.numbers.util.Constants;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.util.UUID;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.servlet.HandlerInterceptor;

/**
 * Interceptor que gestiona los identificadores de transacción en las peticiones HTTP.
 *
 * @author ajuar
 */
@Component
@Slf4j
public class TransactionInterceptor implements HandlerInterceptor {

    /**
     * Se ejecuta antes del controlador: asigna el identificador de transacción.
     *
     * @param request petición
     * @param response respuesta
     * @param handler manejador
     * @return {@code true} si la operación tuvo éxito
     * @throws Exception si ocurre un error
     */
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response,
            Object handler)
            throws Exception {
        String tx = request.getHeader(Constants.HEADER_TRANSACTION_ID_KEY);
        if (!StringUtils.hasText(tx)) {
            tx = UUID.randomUUID().toString();
        }
        log.debug("Tx: {}", tx);
        MDC.put(Constants.TRACE_TX, tx);
        response.setHeader(Constants.HEADER_TRANSACTION_ID_KEY, tx);
        return true;
    }

    /**
     * Se ejecuta al completar la petición.
     * Limpia el contexto MDC al terminar la petición.
     *
     * @param request petición
     * @param response respuesta
     * @param handler manejador
     * @param ex excepción
     * @throws Exception si ocurre un error
     */
    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response,
            Object handler, Exception ex)
            throws Exception {
        MDC.remove(Constants.TRACE_TX);
    }

}
