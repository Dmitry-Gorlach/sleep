/*
 * Copyright (C) 2025 Noom, Inc.
 */
package com.noom.interview.fullstack.sleep.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.parameters.Parameter;
import io.swagger.v3.oas.models.media.StringSchema;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Configuration class for OpenAPI documentation.
 */
@Configuration
public class OpenApiConfig {

    @Value("${spring.application.name:Sleep Application}")
    private String applicationName;

    @Value("${spring.application.version:0.0.1-SNAPSHOT}")
    private String version;

    /**
     * Configures the OpenAPI documentation with application info and global parameters.
     *
     * @return the OpenAPI configuration
     */
    @Bean
    public OpenAPI apiInfo() {
        return new OpenAPI()
                .info(new Info()
                        .title(applicationName)
                        .version(version)
                        .description("API for the Sleep Application"))
                .components(new Components()
                        .addParameters("X-User-Id",
                                new Parameter()
                                        .in("header")
                                        .required(true)
                                        .schema(new StringSchema().format("uuid"))
                                        .description("Current user's UUID")
                        )
                );
    }
}
