package com.juniverse.numbers.config;

import com.juniverse.numbers.filter.TransactionInterceptor;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * The Class FilterConfig.
 * Configuration class for registering web interceptors.
 *
 * @author ajuar
 */
@Configuration
public class FilterConfig implements WebMvcConfigurer {

    /**
     * The txInterceptor.
     */
    private final TransactionInterceptor txInterceptor;

    /**
     * Instantiates a new filter config.
     *
     * @param txInterceptor the transaction interceptor
     */
    public FilterConfig(TransactionInterceptor txInterceptor) {
        this.txInterceptor = txInterceptor;
    }

    /**
     * Adds the interceptors.
     *
     * @param registry the registry
     */
    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(txInterceptor);
    }

}
