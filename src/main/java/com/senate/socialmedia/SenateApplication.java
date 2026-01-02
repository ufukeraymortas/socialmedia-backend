package com.senate.socialmedia;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.core.Ordered;

import java.util.Collections;

@SpringBootApplication
public class SenateApplication {

    public static void main(String[] args) {
        SpringApplication.run(SenateApplication.class, args);
    }

    // 🔥 "BALYOZ" CORS FİLTRESİ - EN YÜKSEK ÖNCELİK 🔥
    @Bean
    public FilterRegistrationBean<CorsFilter> simpleCorsFilter() {
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        CorsConfiguration config = new CorsConfiguration();
        
        // 1. Kimlik bilgilerine izin ver
        config.setAllowCredentials(true);
        
        // 2. Vercel ve Localhost dahil her yere izin ver
        // (setAllowedOrigins yerine setAllowedOriginPatterns kullanıyoruz, daha garantidir)
        config.setAllowedOriginPatterns(Collections.singletonList("*"));
        
        // 3. Tüm metodlara ve başlıklara izin ver
        config.addAllowedHeader("*");
        config.addAllowedMethod("*");
        
        source.registerCorsConfiguration("/**", config);
        
        CorsFilter corsFilter = new CorsFilter(source);
        FilterRegistrationBean<CorsFilter> bean = new FilterRegistrationBean<>(corsFilter);
        
        // 🔥 İŞTE ÇÖZÜM BURADA: Sıralamayı en başa alıyoruz!
        bean.setOrder(Ordered.HIGHEST_PRECEDENCE);
        return bean;
    }
}