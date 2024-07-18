package com.dcm.backend.config;

import ma.gov.mes.framework.keycloak.KeycloakProperties;
import ma.gov.mes.framework.keycloak.jwt.JwtAuthConverter;
import org.keycloak.OAuth2Constants;
import org.keycloak.admin.client.Keycloak;
import org.keycloak.admin.client.KeycloakBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class KeycloakConfig {

    @Autowired
    private ApplicationProperties ap;

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

    @Bean
    Keycloak keycloak() {
        return KeycloakBuilder.builder()
                .serverUrl(keycloakProperties().getAuthServerUrl())
                .realm("master")
                .grantType(OAuth2Constants.PASSWORD)
                .clientId("admin-cli")
                .username(ap.getKeycloakUsername())
                .password(ap.getKeycloakPassword())
                .build();
    }

}
