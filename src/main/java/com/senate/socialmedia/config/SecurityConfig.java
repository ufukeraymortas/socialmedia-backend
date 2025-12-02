package com.senate.socialmedia.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            .csrf(csrf -> csrf.disable()) // API için CSRF kapat
            .headers(headers -> headers.frameOptions(frameOptions -> frameOptions.disable())) // H2 konsolu için
            .authorizeHttpRequests(authz -> authz
                // BURASI ÇOK ÖNEMLİ:
                // Tüm API'lere (/api/**), dosyalara (/uploads/**) ve H2 konsoluna İZİN VER
                .requestMatchers("/api/**", "/uploads/**", "/h2-console/**").permitAll()
                // Diğer her şey de serbest olsun (Test aşamasında sorun çıkmaması için)
                .anyRequest().permitAll() 
            );
            
        return http.build();
    }
}