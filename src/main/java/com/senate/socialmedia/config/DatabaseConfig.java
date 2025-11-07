package com.senate.socialmedia.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import jakarta.annotation.PostConstruct;

// Bu sınıf sadece Render profilimiz (SPRING_PROFILES_ACTIVE=render) açıkken çalışır.
@Profile("render")
@Configuration
public class DatabaseConfig {

    @PostConstruct
    public void setDbProperties() {
        // Render'daki DATABASE_URL değişkenini alıyoruz (Örn: postgres://user:pass@host/db)
        String dbUrl = System.getenv("DATABASE_URL");

        if (dbUrl != null) {
            // Spring Boot'un istediği formata çeviriyoruz: jdbc:postgresql://...
            String jdbcUrl = dbUrl.replace("postgres://", "jdbc:postgresql://");

            // Spring Boot'a bu URL'yi kullanmasını söylüyoruz.
            // Bu, application.properties dosyasını atlar.
            System.setProperty("spring.datasource.url", jdbcUrl);

            // Diğer bilgiler Render tarafından otomatik olarak URL'den ayrıştırılır.
        }
    }
}