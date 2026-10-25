package com.work.converter.filter;

import com.work.converter.util.Constants;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.util.UUID;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.servlet.HandlerInterceptor;

/**
 * The Class TransactionInterceptor.
 * Interceptor for handling transaction IDs in HTTP requests.
 *
 * @author ajuar
 */
@Component
@Slf4j
public class TransactionInterceptor implements HandlerInterceptor {

    /**
     * Pre handle.
     *
     * @param request the request
     * @param response the response
     * @param handler the handler
     * @return true, if successful
     * @throws Exception the exception
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
     * After completion.
     * Cleans up the MDC context after request completion.
     *
     * @param request the request
     * @param response the response
     * @param handler the handler
     * @param ex the exception
     * @throws Exception the exception
     */
    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response,
            Object handler, Exception ex)
            throws Exception {
        MDC.remove(Constants.TRACE_TX);
    }

}
