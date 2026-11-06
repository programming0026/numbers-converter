package com.juniverse.numbers.dto;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * @author Java Universe
 */
@ConfigurationProperties(prefix = "swagger")
@Data
public class ConfigSwaggerDTO {
	/** Título. */
	private String application;

	/** Versión. */
	private String version;

	/**
	 * Ruta.
	 */
	private String path;

}
