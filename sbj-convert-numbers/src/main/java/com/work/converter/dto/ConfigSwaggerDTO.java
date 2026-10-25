package com.work.converter.dto;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * The Class ConfigSwaggerDTO.
 *
 * @author linux
 */
@ConfigurationProperties(prefix = "swagger")
@Data
public class ConfigSwaggerDTO {
	/** The title. */
	private String application;

	/** The version. */
	private String version;

	/**
	 * The path.
	 */
	private String path;

}
