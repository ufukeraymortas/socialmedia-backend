package com.senate.socialmedia;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import jakarta.annotation.PostConstruct; // Yeni eklenecek

@SpringBootApplication
public class SenateApplication {

    public static void main(String[] args) {
        SpringApplication.run(SenateApplication.class, args);
    }
    
    @PostConstruct
    public void setPostgresqlDriver() {
        try {
            Class.forName("org.postgresql.Driver");
        } catch (ClassNotFoundException e) {
            e.printStackTrace(); 
        }
    }
}