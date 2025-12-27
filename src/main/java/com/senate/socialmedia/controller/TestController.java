package com.senate.socialmedia.controller;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.transaction.annotation.Transactional;

@RestController
@RequestMapping("/api/test")
public class TestController {

    @PersistenceContext
    private EntityManager entityManager;

    // 🔥 BALYOZ YÖNTEMİ: SQL İLE ZORLA SİLME 🔥
    @GetMapping("/factory-reset")
    @Transactional
    public String nukeDatabase() {
        try {
            // Bu komut tüm tabloların içini, bağlantılarına bakmaksızın boşaltır.
            entityManager.createNativeQuery(
                "TRUNCATE TABLE " +
                "users, communities, posts, votes, candidates, elections, community_ranks, messages, community_members " +
                "RESTART IDENTITY CASCADE"
            ).executeUpdate();

            return "✅ SİSTEM SQL İLE ZORLA SIFIRLANDI! Veritabanı tertemiz.";
        } catch (Exception e) {
            e.printStackTrace();
            return "❌ HATA: " + e.getMessage();
        }
    }
    
    // Test amaçlı seçim başlatma (İstersen kalsın)
    @GetMapping("/start-elections")
    public String ping() { return "Pong"; }
}