package com.transithub.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;

/**
 * TEMPORARY security setup for Phase 3.
 * Spring Security locks every URL by default, so for now we allow all requests.
 * In Phase 10 we add login (JWT) and role-based rules (USER / ADMIN) here.
 */
@Configuration
public class SecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .cors(Customizer.withDefaults())   // uses the CorsConfigurationSource bean
                .csrf(csrf -> csrf.disable())      // not needed for a token-based API
                .authorizeHttpRequests(auth -> auth.anyRequest().permitAll());
        return http.build();
    }
}
