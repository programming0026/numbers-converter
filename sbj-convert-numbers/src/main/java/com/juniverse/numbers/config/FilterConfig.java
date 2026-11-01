package com.juniverse.numbers.config;

import com.juniverse.numbers.filter.TransactionInterceptor;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * Configuración que registra los interceptores web.
 *
 * @author ajuar
 */
@Configuration
public class FilterConfig implements WebMvcConfigurer {

    /**
     * Interceptor de identificadores de transacción.
     */
    private final TransactionInterceptor txInterceptor;

    public FilterConfig(TransactionInterceptor txInterceptor) {
        this.txInterceptor = txInterceptor;
    }

    /**
     * Registra los interceptores.
     *
     * @param registry registro de interceptores o de CORS
     */
    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(txInterceptor);
    }

}
