package com.senate.socialmedia.config; // Sizin paket adınız

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
        .csrf(csrf -> csrf.disable()) // CSRF korumasını kapat (API'ler için gerekli)

        // Tüm API ve uploads yollarına İZİN VER (Giriş zorunluluğu yok)
        .authorizeHttpRequests(authz -> authz
            .requestMatchers("/api/**", "/uploads/**", "/h2-console/**").permitAll()
            .anyRequest().authenticated()
        );
        return http.build();
    }
}