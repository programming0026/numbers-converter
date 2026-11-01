package com.juniverse.numbers.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 *
 * @author ajuar
 */
@Configuration
public class CorsConfig implements WebMvcConfigurer {

    /**
     * Registra las reglas de CORS.
     *
     * @param registry registro de interceptores o de CORS
     */
    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**")
                .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS")
                .allowedOrigins("*")
                .allowedHeaders("*")
                .maxAge(3600);
    }

}
