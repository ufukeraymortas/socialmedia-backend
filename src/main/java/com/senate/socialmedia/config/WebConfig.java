package com.senate.socialmedia.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**") // Tüm adreslere izin ver (login, register, vs.)
                .allowedOrigins(
                    "https://senato.vercel.app", // Senin Canlı Siten (Şu anki hatanın sebebi bu)
                    "http://localhost:5500",     // VS Code Live Server
                    "http://127.0.0.1:5500",     
                    "http://localhost:3000"      // Diğer test ortamları
                )
                .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS") // Tüm metodlara izin ver
                .allowedHeaders("*") // Tüm başlıklara izin ver
                .allowCredentials(true); // Cookie ve yetkilendirme bilgilerine izin ver
    }
}