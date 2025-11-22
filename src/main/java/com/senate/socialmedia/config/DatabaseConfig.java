package com.senate.socialmedia.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile; // Bu artık gerekmiyor, çünkü Render'da tek profil aktif
import jakarta.annotation.PostConstruct;

@Configuration
public class DatabaseConfig {

    @PostConstruct
    public void setDbProperties() {
        // RENDER'IN OTOMATİK OLUŞTURDUĞU URL'Yİ OKU
        String dbUrl = System.getenv("DATABASE_URL");

        if (dbUrl != null) {
            // SADECE RENDER'A ÖZEL AYARLAR
            String jdbcUrl = dbUrl.replace("postgres://", "jdbc:postgresql://");
            
            // 1. Render'ın URL'sini sisteme ata (Bu en önemli satır)
            System.setProperty("spring.datasource.url", jdbcUrl); 
            
            // 2. Diğer ayarları zorla PostgreSQL'e geçir
            System.setProperty("spring.datasource.driver-class-name", "org.postgresql.Driver");
            System.setProperty("spring.jpa.database-platform", "org.hibernate.dialect.PostgreSQLDialect");
            System.setProperty("spring.jpa.hibernate.ddl-auto", "update");
        } 
        
        // ELSE BLOĞUNU KALDIRDIK: Artık H2'ye bağlanmaya çalışmayacak. 
        // Eğer Render DB'yi bulamazsa, uygulama doğru şekilde (URL eksik hatasıyla) çökecektir.
    }
}