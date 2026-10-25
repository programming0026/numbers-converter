package com.work.converter.config;

import com.work.converter.dto.ConfigSwaggerDTO;
import com.work.converter.util.Constants;
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
     * Configuracion Swagger.
     */
    private final ConfigSwaggerDTO config;

    public SwaggerConfig(ConfigSwaggerDTO config) {
        this.config = config;
    }

    /**
     * Open API.
     *
     * @return the open API
     */
    @Bean
    OpenAPI openAPI() {
        return new OpenAPI().info(getInfo()).addServersItem(new Server().url(config.getPath()));
    }

    /**
     * Gets the info.
     *
     * @return the info
     */
    private Info getInfo() {
        Info info = new Info();
        info.setTitle(config.getApplication());
        info.setDescription(Constants.SWAGGER_DESCRIPTION);
        info.setVersion(config.getVersion());
        info.setContact(getContact());
        return info;
    }

    /**
     * Gets the contact.
     *
     * @return the contact
     */
    private Contact getContact() {
        return new Contact().name(Constants.SWAGGER_NAME)
                .url(Constants.SWAGGER_URL)
                .email(Constants.SWAGGER_EMAIL);
    }

}
