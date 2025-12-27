package com.senate.socialmedia.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        // Tüm adreslere (/**) gelen isteklere izin ver
        registry.addMapping("/**")
                .allowedOrigins("*") // Vercel, Localhost vs. her yerden erişime izin ver
                .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS") // Tüm işlemleri aç
                .allowedHeaders("*"); // Tüm başlıkları kabul et
    }
}