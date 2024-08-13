package com.dcm.backend.config;

import org.keycloak.admin.client.Keycloak;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;

@TestConfiguration
public class TestSecurityConfig {

    @Bean
    public UserDetailsService userDetailsService() {
        InMemoryUserDetailsManager manager = new InMemoryUserDetailsManager();
        UserDetails user =
                User.withUsername("user1").password("password").roles("web_docs_import",
                        "web_docs_delete", "web_docs_modify", "web_docs_duplicate",
                        "web_docs_download", "web_docs_copy_link", "web_docs_share").build();
        manager.createUser(User.withUserDetails(user).build());

        return manager;
    }

    @Bean
    public Keycloak keycloak() {
        // return a mock
        return Mockito.mock(Keycloak.class);
    }
}
