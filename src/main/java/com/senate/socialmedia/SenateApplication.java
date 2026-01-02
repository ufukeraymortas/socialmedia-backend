package com.senate.socialmedia;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@SpringBootApplication
public class SenateApplication {

    public static void main(String[] args) {
        SpringApplication.run(SenateApplication.class, args);
    }

    // 🔥 KESİN CORS ÇÖZÜMÜ: BURADAKİ AYAR HER ŞEYİ EZER 🔥
    @Bean
    public WebMvcConfigurer corsConfigurer() {
        return new WebMvcConfigurer() {
            @Override
            public void addCorsMappings(CorsRegistry registry) {
                registry.addMapping("/**") // Tüm linklere izin ver
                        .allowedOrigins("*") // Vercel, Localhost, her yerden gelen isteği kabul et
                        .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS", "HEAD", "PATCH") // Tüm metodları aç
                        .allowedHeaders("*"); // Tüm başlıkları kabul et
            }
        };
    }
}