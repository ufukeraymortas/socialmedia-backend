package com.senate.socialmedia.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder; // 🔥 BU EKLENDİ
import org.springframework.security.crypto.password.PasswordEncoder;     // 🔥 BU EKLENDİ
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.cors.CorsConfigurationSource;
import java.util.Arrays;
import java.util.Collections;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            .csrf(csrf -> csrf.disable()) // Güvenlik duvarını (CSRF) indir
            .cors(cors -> cors.configurationSource(corsConfigurationSource())) // CORS ayarlarını yükle
            .authorizeHttpRequests(auth -> auth
                .requestMatchers("/api/**").permitAll() // API'lere herkes erişsin
                .anyRequest().permitAll() // Diğer her şeye de izin ver
            );

        return http.build();
    }

    // 🔥 İŞTE EKSİK OLAN PARÇA BU: ŞİFRELEYİCİ TANIMI 🔥
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    // CORS AYARLARI (Vercel Erişimi İçin)
    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        
        // Vercel, Localhost vb. her yerden gelen isteği kabul et
        configuration.setAllowedOriginPatterns(Collections.singletonList("*"));
        
        // Tüm metodlara izin ver (GET, POST, PUT, DELETE...)
        configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "OPTIONS", "HEAD", "PATCH"));
        
        // Tüm başlıklara izin ver
        configuration.setAllowedHeaders(Arrays.asList("*"));
        
        // Kimlik bilgilerine izin ver
        configuration.setAllowCredentials(true);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }
}