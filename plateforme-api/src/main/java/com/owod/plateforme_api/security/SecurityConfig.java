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

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class SecurityConfig {

    @Autowired
    private AuthTokenFilter authTokenFilter;

    @Autowired
    private UserService userService;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(AbstractHttpConfigurer::disable) // ActiveRecord CSRF pour une API stateless
                .cors(cors -> cors.configurationSource(request -> {
                    var corsConfig = new CorsConfiguration();
                    corsConfig.setAllowedOrigins(Arrays.asList("http://localhost:4200", "https://owod.aipda-design.org"));

                    corsConfig.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "OPTIONS", "HEAD"));
                    corsConfig.setAllowedHeaders(Arrays.asList("Authorization",
                            "Content-Type",
                            "Accept",
                            "Origin",
                            "Access-Control-Request-Method",
                            "Access-Control-Request-Headers"));
                    corsConfig.setExposedHeaders(Arrays.asList(
                            "Access-Control-Allow-Origin",
                            "Access-Control-Allow-Credentials"
                    ));
                    corsConfig.setMaxAge(3600L);
                    corsConfig.setAllowCredentials(true);
                    return corsConfig;
                }))
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS)) // Pas de sessions
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/auth/login", "/auth/logout", "/auth/register", "/designers/all", "/uploads/**", "/weekly", "/password/**", "/contact").permitAll() // Routes publiques
                        .requestMatchers("/users/**", "/designers/**", "/auth/me").authenticated()
                        .anyRequest().authenticated() // Toute autre route nécessite une authentification
                );

        http.addFilterBefore(authTokenFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authConfig) throws Exception {
        return authConfig.getAuthenticationManager();
    }

    @Bean
    public UserDetailsService userDetailsService() {
        return username -> {
            User user = userService.findByEmail(username).orElseThrow(() ->
                    new UsernameNotFoundException("User not found: " + username));

            var authorities = user.getRoles().stream()
                    .map(role ->  new org.springframework.security.core.authority.SimpleGrantedAuthority(role.authority()))
                    .collect(Collectors.toList());

            // Spring-UserDetails
            return org.springframework.security.core.userdetails.User
                    .withUsername(user.getEmail())
                    .password(user.getPassword())
                    .authorities(authorities)
                    .build();
        };
    }
}
