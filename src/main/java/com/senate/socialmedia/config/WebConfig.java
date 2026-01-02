package com.senate.socialmedia.config; // Kendi paket isminle değiştir

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
@EnableWebMvc
public class WebConfig implements WebMvcConfigurer {

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**") // Tüm adreslere izin ver
                .allowedOrigins(
                    "https://senato.vercel.app", // Senin Vercel siten
                    "http://localhost:5500",     // VS Code Live Server
                    "http://127.0.0.1:5500",
                    "http://localhost:3000"      // Local testler
                )
                .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS") // İzin verilen metodlar
                .allowedHeaders("*") // Tüm başlıklara izin ver
                .allowCredentials(true); // Cookie ve yetkilendirmeye izin ver
    }
}