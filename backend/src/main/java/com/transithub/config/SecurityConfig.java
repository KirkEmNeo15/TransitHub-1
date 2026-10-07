package com.transithub.config;

import com.transithub.repository.UserRepository;
import com.transithub.security.JwtAuthenticationFilter;
import com.transithub.security.JwtService;
import com.transithub.security.RestAccessDeniedHandler;
import com.transithub.security.RestAuthenticationEntryPoint;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

/**
 * Who can call what.
 *  - Anyone: register, login, and all READ endpoints (routes, stops, alerts, transportations, stats).
 *  - Any logged-in user: favorites, reports, "me".
 *  - ADMIN only: everything under /api/admin and every create/update/delete on routes,
 *    stops, transportations and alerts.
 */
@Configuration
public class SecurityConfig {

    /** BCrypt turns a password into a one-way hash. We store only the hash. */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    /** Checks an email + password at login (using CustomUserDetailsService and the PasswordEncoder). */
    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration configuration) throws Exception {
        return configuration.getAuthenticationManager();
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http,
                                                   JwtService jwtService,
                                                   UserRepository userRepository) throws Exception {
        http
                .cors(Customizer.withDefaults())   // uses the CorsConfigurationSource bean
                .csrf(csrf -> csrf.disable())      // no cookies or sessions: login is a token in a header
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .exceptionHandling(errors -> errors
                        .authenticationEntryPoint(new RestAuthenticationEntryPoint())
                        .accessDeniedHandler(new RestAccessDeniedHandler()))
                .authorizeHttpRequests(auth -> auth
                        // open to everyone
                        .requestMatchers(HttpMethod.POST, "/api/auth/register", "/api/auth/login").permitAll()
                        .requestMatchers(HttpMethod.GET, "/api/health", "/api/stats").permitAll()
                        .requestMatchers(HttpMethod.GET,
                                "/api/routes", "/api/routes/**",
                                "/api/stops", "/api/stops/**",
                                "/api/transportations", "/api/transportations/**",
                                "/api/alerts", "/api/alerts/**").permitAll()
                        // admins only
                        .requestMatchers("/api/admin/**").hasRole("ADMIN")
                        .requestMatchers(
                                "/api/routes", "/api/routes/**",
                                "/api/stops", "/api/stops/**",
                                "/api/transportations", "/api/transportations/**",
                                "/api/alerts", "/api/alerts/**").hasRole("ADMIN")
                        // everything else (favorites, reports, /api/auth/me) needs a login
                        .anyRequest().authenticated())
                .addFilterBefore(new JwtAuthenticationFilter(jwtService, userRepository),
                        UsernamePasswordAuthenticationFilter.class);
        return http.build();
    }
}
