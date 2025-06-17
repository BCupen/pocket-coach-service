package com.bcupen.pocket_coach_service.auth.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class SecurityConfig {

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .authorizeHttpRequests(auth -> auth.anyRequest().permitAll())  // Allow all requests
                .csrf(AbstractHttpConfigurer::disable)  // Disable CSRF for testing (optional)
                .httpBasic(AbstractHttpConfigurer::disable)  // Disable basic auth
                .formLogin(AbstractHttpConfigurer::disable);  // Disable form login

        return http.build();
    }
}
