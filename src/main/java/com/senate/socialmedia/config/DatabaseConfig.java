package com.senate.socialmedia.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import jakarta.annotation.PostConstruct;

@Configuration
public class DatabaseConfig {

    @PostConstruct
    public void setDbProperties() {
        // RENDER'IN OTOMATİK OLUŞTURDUĞU URL'Yİ BURADAN OKUYORUZ
        String dbUrl = System.getenv("DATABASE_URL");

        if (dbUrl != null) {
            // Spring Boot'un istediği formata çeviriyoruz: jdbc:postgresql://...
            String jdbcUrl = dbUrl.replace("postgres://", "jdbc:postgresql://");

            // Spring Boot'a bu URL'yi kullanmasını söylüyoruz.
            System.setProperty("spring.datasource.url", jdbcUrl);
        } else {
             // Eğer DATABASE_URL Render'dan gelmezse, yerel çalışmak için H2'ye geri dön
             System.setProperty("spring.datasource.url", "jdbc:h2:mem:testdb");
        }

        // Tüm diğer ayarları manuel olarak set ediyoruz.
        System.setProperty("spring.datasource.driver-class-name", "org.postgresql.Driver");
        System.setProperty("spring.jpa.database-platform", "org.hibernate.dialect.PostgreSQLDialect");
        System.setProperty("spring.jpa.hibernate.ddl-auto", "update");
    }
}