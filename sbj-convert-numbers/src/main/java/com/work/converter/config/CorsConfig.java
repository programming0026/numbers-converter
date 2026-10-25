package com.work.converter.config;

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
     * Adds the cors mappings.
     *
     * @param registry the registry
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
