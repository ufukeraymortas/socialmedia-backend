package com.senate.socialmedia; // 1. Paket ismini klasöre uygun hale getirdik

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    // Giriş yaparken kullanıcıyı bulmak için:
    Optional<User> findByUsername(String username);

    // Kayıt olurken "Bu isim kullanılıyor mu?" diye hızlıca bakmak için:
    // (Veritabanından tüm kullanıcıyı çekmek yerine sadece var mı yok mu diye bakar, çok hızlıdır)
    boolean existsByUsername(String username);
}