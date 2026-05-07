package com.management.usermanagement.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@Profile("!test")
public class SecurityConfig {

    private final CustomOAuth2UserService customOAuth2UserService;
    private final org.springframework.beans.factory.ObjectProvider<org.springframework.security.oauth2.client.registration.ClientRegistrationRepository> clientRegistrationRepositoryProvider;

    public SecurityConfig(org.springframework.beans.factory.ObjectProvider<CustomOAuth2UserService> customOAuth2UserServiceProvider,
                          org.springframework.beans.factory.ObjectProvider<org.springframework.security.oauth2.client.registration.ClientRegistrationRepository> clientRegistrationRepositoryProvider) {
        this.customOAuth2UserService = customOAuth2UserServiceProvider.getIfAvailable();
        this.clientRegistrationRepositoryProvider = clientRegistrationRepositoryProvider;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            .csrf().disable()
            // TEMPORARY: permit all for local testing
            .authorizeHttpRequests(authorize -> authorize.anyRequest().permitAll())
            ;

        return http.build();
    }
}
