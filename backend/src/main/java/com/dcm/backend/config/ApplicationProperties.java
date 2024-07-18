package com.dcm.backend.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Data
@Component
@ConfigurationProperties(prefix = "application")
public class ApplicationProperties {

    private String keycloakUsername;

    private String keycloakPassword;

    private String elasticsearchHost;

    private boolean useElasticsearch;

}