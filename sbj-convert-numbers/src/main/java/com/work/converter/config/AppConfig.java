package com.work.converter.config;

import com.work.converter.service.LoggingServiceImpl;
import org.springframework.context.annotation.Bean;
import com.work.converter.service.LoggingService;
import org.springframework.context.annotation.Configuration;

/**
 * The Class AppConfig.
 *
 * @author ajuar
 */
@Configuration
public class AppConfig {

    /**
     * Loggin Service.
     *
     * @return LoggingService
     */
    @Bean
    LoggingService loggingService() {
        return new LoggingServiceImpl();
    }
}
