package com.dcm.backend.config;

import ma.gov.mes.framework.keycloak.KeycloakProperties;
import ma.gov.mes.framework.keycloak.jwt.JwtAuthConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class KeycloakConfig {

    @Bean
    public KeycloakProperties keycloakProperties() {
        return new KeycloakProperties();
    }

    @Bean
    public JwtAuthConverter jwtAuthConverter() {
        return new JwtAuthConverter(
                keycloakProperties()
        );
    }

}
