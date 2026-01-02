package com.senate.socialmedia.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
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
            // 1. CSRF Korumasını Kapat (API'ler için gereklidir)
            .csrf(csrf -> csrf.disable())
            
            // 2. CORS Ayarlarını Aktif Et
            .cors(cors -> cors.configurationSource(corsConfigurationSource()))
            
            // 3. Hangi sayfalara şifresiz girilebilir?
            .authorizeHttpRequests(auth -> auth
                .requestMatchers("/api/**").permitAll() // /api ile başlayan HER ŞEYE izin ver
                .anyRequest().permitAll() // Diğer her şeye de izin ver (Test için tam erişim)
            );

        return http.build();
    }

    // 🔥 KAPSAMLI CORS AYARI 🔥
    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        
        // Vercel ve her yerden gelen isteklere izin ver
        configuration.setAllowedOriginPatterns(Collections.singletonList("*"));
        
        // Tüm metodlara izin ver (GET, POST, PUT, DELETE...)
        configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "OPTIONS", "HEAD"));
        
        // Tüm başlıklara (Header) izin ver
        configuration.setAllowedHeaders(Arrays.asList("*"));
        
        // Çerezlere izin ver (Gerekirse)
        configuration.setAllowCredentials(true);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }
}