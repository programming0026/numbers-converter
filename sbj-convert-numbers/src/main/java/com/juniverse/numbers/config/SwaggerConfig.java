package com.juniverse.numbers.config;

import com.juniverse.numbers.dto.ConfigSwaggerDTO;
import com.juniverse.numbers.util.Constants;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

/**
 *
 * @author ajuar
 */
@Configuration
@PropertySource({"classpath:application.properties"})
@EnableConfigurationProperties(ConfigSwaggerDTO.class)
public class SwaggerConfig {

    /**
     * Configuración de Swagger.
     */
    private final ConfigSwaggerDTO config;

    public SwaggerConfig(ConfigSwaggerDTO config) {
        this.config = config;
    }

    /**
     * Configuración de OpenAPI.
     *
     * @return configuración de OpenAPI
     */
    @Bean
    OpenAPI openAPI() {
        return new OpenAPI().info(getInfo()).addServersItem(new Server().url(config.getPath()));
    }

    private Info getInfo() {
        Info info = new Info();
        info.setTitle(config.getApplication());
        info.setDescription(Constants.SWAGGER_DESCRIPTION);
        info.setVersion(config.getVersion());
        info.setContact(getContact());
        return info;
    }

    /**
     * Obtiene los datos de contacto.
     *
     * @return datos de contacto
     */
    private Contact getContact() {
        return new Contact().name(Constants.SWAGGER_NAME)
                .url(Constants.SWAGGER_URL)
                .email(Constants.SWAGGER_EMAIL);
    }

}
