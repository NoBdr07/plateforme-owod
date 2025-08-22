package com.owod.plateforme_api.security;

import com.owod.plateforme_api.models.entities.User;
import com.owod.plateforme_api.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;

import java.util.Arrays;
import java.util.stream.Collectors;

/**
 * Security configuration for HTTP endpoints, CORS, session management, and user authentication.
 * <p>
 * Defines the security filter chain, password encoding, authentication manager, and user details service.
 */
@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class SecurityConfig {

    @Autowired
    private AuthTokenFilter authTokenFilter;

    @Autowired
    private UserService userService;

    /**
     * Configures the security filter chain for HTTP requests.
     * <p>
     * Disables CSRF, configures CORS, sets stateless session management, defines public and protected endpoints,
     * and registers the JWT authentication filter.
     *
     * @param http the HttpSecurity to configure
     * @return the configured SecurityFilterChain
     * @throws Exception if an error occurs during configuration
     */
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(AbstractHttpConfigurer::disable)
                .cors(cors -> cors.configurationSource(request -> {
                    var corsConfig = new CorsConfiguration();
                    corsConfig.setAllowedOrigins(Arrays.asList(
                            "http://localhost:4200", "https://owod.aipda-design.org"));
                    corsConfig.setAllowedMethods(Arrays.asList(
                            "GET", "POST", "PUT", "DELETE", "OPTIONS", "HEAD"));
                    corsConfig.setAllowedHeaders(Arrays.asList(
                            "Authorization", "Content-Type", "Accept", "Origin",
                            "Access-Control-Request-Method", "Access-Control-Request-Headers"));
                    corsConfig.setExposedHeaders(Arrays.asList(
                            "Access-Control-Allow-Origin", "Access-Control-Allow-Credentials"));
                    corsConfig.setMaxAge(3600L);
                    corsConfig.setAllowCredentials(true);
                    return corsConfig;
                }))
                .sessionManagement(session ->
                        session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers(
                                "/auth/login", "/auth/logout", "/auth/register",
                                "/designers/all", "/uploads/**", "/error", "/weekly",
                                "/password/**", "/contact")
                        .permitAll()
                        .requestMatchers(
                                "/users/**", "/designers/**", "/auth/me")
                        .authenticated()
                        .anyRequest()
                        .authenticated()
                );

        http.addFilterBefore(authTokenFilter, UsernamePasswordAuthenticationFilter.class);
        return http.build();
    }

    /**
     * Password encoder bean using BCrypt hashing algorithm.
     *
     * @return a BCryptPasswordEncoder instance
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    /**
     * Exposes the AuthenticationManager bean to be used for authentication operations.
     *
     * @param authConfig the authentication configuration
     * @return the AuthenticationManager
     * @throws Exception if the authentication manager cannot be created
     */
    @Bean
    public AuthenticationManager authenticationManager(
            AuthenticationConfiguration authConfig) throws Exception {
        return authConfig.getAuthenticationManager();
    }

    /**
     * UserDetailsService for loading user-specific data during authentication.
     * <p>
     * Retrieves the User by email, maps roles to SimpleGrantedAuthority, and builds a Spring Security User.
     *
     * @return the configured UserDetailsService
     */
    @Bean
    public UserDetailsService userDetailsService() {
        return username -> {
            User user = userService.findByEmail(username)
                    .orElseThrow(() ->
                            new UsernameNotFoundException("User not found: " + username)
                    );

            var authorities = user.getRoles().stream()
                    .map(role ->
                            new org.springframework.security.core.authority.SimpleGrantedAuthority(
                                    role.authority()))
                    .collect(Collectors.toList());

            return org.springframework.security.core.userdetails.User
                    .withUsername(user.getEmail())
                    .password(user.getPassword())
                    .authorities(authorities)
                    .build();
        };
    }
}
