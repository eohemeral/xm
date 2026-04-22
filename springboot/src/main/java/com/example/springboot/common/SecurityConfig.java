package com.example.springboot.common;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            .csrf(csrf -> csrf.disable())
            .authorizeHttpRequests(auth -> auth
                .requestMatchers("/user/**").permitAll()
                .requestMatchers("/course/**").permitAll()
                .requestMatchers("/videos/**").permitAll()
                .requestMatchers("/images/**").permitAll()
                .requestMatchers("/video/**").permitAll()
                .requestMatchers("/community/**").permitAll()
                .requestMatchers("/admin/**").permitAll()
                .anyRequest().authenticated()
            );
        return http.build();
    }
}
