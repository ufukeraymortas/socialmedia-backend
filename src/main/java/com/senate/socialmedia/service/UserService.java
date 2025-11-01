package com.senate.socialmedia.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.senate.socialmedia.User; // User modelimizi import ediyoruz
import com.senate.socialmedia.UserRepository; // Repository'mizi import ediyoruz
import java.util.Optional;

@Service // 1. Spring'e bunun bir Servis sınıfı olduğunu söyler
public class UserService {

    // 2. Dependency Injection: Spring bize bu nesneleri otomatik olarak verecek
    
    @Autowired
    private UserRepository userRepository; // 'users' tablosuyla konuşmak için

    @Autowired
    private PasswordEncoder passwordEncoder; // Şifreleri hash'lemek için (SecurityConfig'den gelir)

    /**
     * Yeni bir kullanıcıyı kaydetmek için gereken iş mantığı.
     */
    public User registerUser(String username, String rawPassword) {
        
        // İş Mantığı 1: Kullanıcı adı zaten alınmış mı?
        Optional<User> existingUser = userRepository.findByUsername(username);
        if (existingUser.isPresent()) {
            // Hata fırlatmak daha iyidir, şimdilik basit tutalım
            throw new RuntimeException("Bu kullanıcı adı zaten alınmış!");
        }

        // İş Mantığı 2: Şifre geçerli mi? (Boş olmamalı vb.)
        if (rawPassword == null || rawPassword.isEmpty()) {
            throw new RuntimeException("Şifre boş olamaz!");
        }
        
        // 3. GÜVENLİK: Şifreyi kaydetmeden önce HASH'LE
        String hashedPassword = passwordEncoder.encode(rawPassword);

        // 4. Yeni User nesnesini oluştur
        User newUser = new User();
        newUser.setUsername(username);
        newUser.setPassword(hashedPassword); // Veritabanına HASH'lenmiş şifreyi kaydet

        // 5. Repository kullanarak veritabanına kaydet
        return userRepository.save(newUser);
    }
    
    public User loginUser(String username, String rawPassword) {
        
        // 1. Adım: Kullanıcıyı veritabanında kullanıcı adına göre bul
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("Kullanıcı adı veya şifre hatalı!"));
        
        // 2. Adım (En Önemlisi): ŞİFRE KONTROLÜ
        // passwordEncoder.matches() metodu, kullanıcının girdiği 'rawPassword' ile
        // veritabanındaki şifrelenmiş 'user.getPassword()' şifresini
        // GÜVENLİ bir şekilde karşılaştırır.
        if (passwordEncoder.matches(rawPassword, user.getPassword())) {
            // 3. Adım: Şifre doğruysa, kullanıcıyı geri döndür
            return user;
        } else {
            // 4. Adım: Şifre yanlışsa, hata fırlat
            throw new RuntimeException("Kullanıcı adı veya şifre hatalı!");
        }
    }

} // Bu, sınıfın en son kapanış parantezidir
    
    // Daha sonra buraya loginUser(), findUserById() gibi metotlar da ekleyeceğiz.