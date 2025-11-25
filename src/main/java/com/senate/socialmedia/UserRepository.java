package com.senate.socialmedia;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional; // 'null' yerine daha güvenli bir tip

@Repository // Spring'e bunun bir veritabanı bileşeni (Repository) olduğunu söyler
public interface UserRepository extends JpaRepository<User, Long> {

    // --- JpaRepository sayesinde ---
    // save() -> (Kullanıcı kaydet/güncelle)
    // findById() -> (ID'ye göre kullanıcı bul)
    // findAll() -> (Tüm kullanıcıları bul)
    // delete() -> (Kullanıcı sil)
    // ...gibi metotlar bize OTOMATİK olarak geldi!

    // --- Kendi Özel Metodumuzu Ekleyelim ---
    // Spring Data, metodun isminden ne yapması gerektiğini anlar:
    // "Kullanıcı adına (username) göre bir kullanıcı bul"
    Optional<User> findByUsername(String username); 
}