package com.senate.socialmedia.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile; // Dosya yükleme için

import com.senate.socialmedia.User;
import com.senate.socialmedia.UserRepository;
import java.util.Optional;

@Service
public class UserService {
    
    @Autowired
    private UserRepository userRepository;
    
    @Autowired
    private PasswordEncoder passwordEncoder;
    
    // Profil resmini diske kaydetmek için eklenen servis
    @Autowired 
    private FileStorageService fileStorageService; 
    
    // PostRepository'nin de kullanılması gerekiyor ancak bu metodun içinde kullanmadığımız için burada kalabilir.
    // Eğer PostService'de olsaydı oraya eklerdik.

    /**
     * Yeni bir kullanıcıyı kaydetmek için gereken iş mantığı.
     */
    public User registerUser(String username, String rawPassword) {
        
        // İş Mantığı 1: Kullanıcı adı zaten alınmış mı diye kontrol et
        Optional<User> existingUser = userRepository.findByUsername(username);
        if (existingUser.isPresent()) {
            throw new RuntimeException("Bu kullanıcı adı zaten alınmış!");
        }

        if (rawPassword == null || rawPassword.isEmpty()) {
            throw new RuntimeException("Şifre boş olamaz!");
        }
        
        // GÜVENLİK: Şifreyi HASH'LE
        String hashedPassword = passwordEncoder.encode(rawPassword);

        // Yeni User nesnesini oluştur
        User newUser = new User();
        newUser.setUsername(username);
        newUser.setPassword(hashedPassword);
        
        // Yeni eklenen profil alanlarına ilk değerleri ata
        newUser.setTitle(""); 
        newUser.setBio("");
        
        return userRepository.save(newUser);
    }
    
    /**
     * Kullanıcı girişi için gereken iş mantığı.
     */
    public User loginUser(String username, String rawPassword) {
        
        // 1. Kullanıcıyı veritabanında kullanıcı adına göre bul
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("Kullanıcı adı veya şifre hatalı!"));
        
        // 2. ŞİFRE KONTROLÜ
        if (passwordEncoder.matches(rawPassword, user.getPassword())) {
            return user; // Şifre doğruysa kullanıcıyı geri döndür
        } else {
            throw new RuntimeException("Kullanıcı adı veya şifre hatalı!");
        }
    }
    
    /**
     * Kullanıcının profilini (PP, ünvan, bio) günceller. (YENİ METOT)
     */
    public User updateProfile(Long userId, String title, String bio, MultipartFile profilePicture) {
        // 1. Güncellenecek kullanıcıyı ID ile bul
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("Kullanıcı bulunamadı."));
        
        // 2. Alanları güncelle (null gelirse güncellemeyi atlarız, ama Controller'dan hepsi geleceği için direkt atama yapıyoruz)
        if (title != null) {
            user.setTitle(title);
        }
        if (bio != null) {
            user.setBio(bio);
        }

        // 3. PP yükleme mantığı
        if (profilePicture != null && !profilePicture.isEmpty()) {
            // Dosyayı FileStorageService kullanarak diske kaydet
            String fileName = fileStorageService.storeFile(profilePicture);
            
            // Profil resminin URL'sini veritabanına kaydet
            user.setProfilePictureUrl(fileName);
        }
        
        // 4. Veritabanına kaydet ve güncellenmiş kullanıcıyı geri döndür
        return userRepository.save(user);
    }
    
    // Kullanıcıyı ID ile bulmak için yardımcı metot
    public User findById(Long userId) {
        return userRepository.findById(userId)
            .orElseThrow(() -> new RuntimeException("Kullanıcı bulunamadı."));
    }
}