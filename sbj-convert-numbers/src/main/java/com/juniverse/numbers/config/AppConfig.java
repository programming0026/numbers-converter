package com.juniverse.numbers.config;

import com.juniverse.numbers.service.LoggingServiceImpl;
import org.springframework.context.annotation.Bean;
import com.juniverse.numbers.service.LoggingService;
import org.springframework.context.annotation.Configuration;

/**
 * @author ajuar
 */
@Configuration
public class AppConfig {

    /**
     * Servicio de registro.
     *
     * @return LoggingService
     */
    @Bean
    LoggingService loggingService() {
        return new LoggingServiceImpl();
    }
}
