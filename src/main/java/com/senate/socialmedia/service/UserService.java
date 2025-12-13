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
    
    
 // UserService.java 
    public User updateUserProfile(Long userId, String title, String bio, MultipartFile profilePicture, MultipartFile headerPicture) {
        User user = userRepository.findById(userId).orElseThrow(() -> new RuntimeException("Kullanıcı yok"));
        
        if(title != null) user.setTitle(title);
        if(bio != null) user.setBio(bio);
        
        // Profil Resmi
        if(profilePicture != null && !profilePicture.isEmpty()) {
            String ppName = fileStorageService.storeFile(profilePicture);
            user.setProfilePictureUrl(ppName);
        }

        // YENİ: Header Resmi
        if(headerPicture != null && !headerPicture.isEmpty()) {
            String headerName = fileStorageService.storeFile(headerPicture);
            user.setHeaderUrl(headerName);
        }

        return userRepository.save(user);
    }
    
    // Kullanıcıyı ID ile bulmak için yardımcı metot
    public User findById(Long userId) {
        return userRepository.findById(userId)
            .orElseThrow(() -> new RuntimeException("Kullanıcı bulunamadı."));
    }
    
    public void followUser(Long currentUserId, Long targetUserId) {
        if(currentUserId.equals(targetUserId)) throw new RuntimeException("Kendini takip edemezsin!");
        
        User currentUser = userRepository.findById(currentUserId).orElseThrow();
        User targetUser = userRepository.findById(targetUserId).orElseThrow();

        currentUser.getFollowing().add(targetUser);
        userRepository.save(currentUser);
    }

    public void unfollowUser(Long currentUserId, Long targetUserId) {
        User currentUser = userRepository.findById(currentUserId).orElseThrow();
        User targetUser = userRepository.findById(targetUserId).orElseThrow();

        currentUser.getFollowing().remove(targetUser);
        userRepository.save(currentUser);
    }

    public boolean isFollowing(Long currentUserId, Long targetUserId) {
        User currentUser = userRepository.findById(currentUserId).orElseThrow();
        // Takip ettiklerim listesinde bu kişi var mı?
        return currentUser.getFollowing().stream().anyMatch(u -> u.getId().equals(targetUserId));
    }
}