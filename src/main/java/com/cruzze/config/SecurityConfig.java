package com.cruzze.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            .csrf(csrf -> csrf.disable()) // Disable CSRF (good for API-only backend)
            .authorizeHttpRequests(auth -> auth
                .anyRequest().permitAll()  // ✅ Allow all requests (since you're using Clerk JWTs)
            )
            .formLogin(form -> form.disable())   // ✅ Disable login form
            .httpBasic(basic -> basic.disable()); // ✅ Disable Basic Auth

        return http.build();
    }
}

