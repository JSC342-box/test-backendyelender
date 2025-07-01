package com.cruzze.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            .csrf(csrf -> csrf.disable())
            .authorizeHttpRequests(auth -> auth
                .requestMatchers("/users/createUsers","/drivers/createDrivers",
                		"/drivers/update-location/{driverId}","/drivers/get/{id}","/vehicles/registerVehicle","/rides/request","/rides/accept").permitAll()  // allow unauthenticated access to /users endpoints
                .anyRequest().authenticated()
            )
            .httpBasic(Customizer.withDefaults()); // use basic auth only for protected endpoints

        return http.build();
    }
}
