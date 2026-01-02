package com.senate.socialmedia.controller;

import com.senate.socialmedia.User;
import com.senate.socialmedia.UserRepository;
import com.senate.socialmedia.service.FileStorageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import java.util.Optional;

// 1. CORS AYARI: Hem Vercel'e hem de kendi bilgisayarına (Localhost) izin veriyoruz.
// allowCredentials = "true" ekledik, cookie/session işlemleri için önemlidir.
@CrossOrigin(origins = {"https://senato.vercel.app", "http://localhost:3000", "http://127.0.0.1:5500"}, allowCredentials = "true")
@RestController
@RequestMapping("/api") // 2. ADRES DÜZENİ: Tüm adreslerin başına '/api' ekledik.
public class UserController {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private FileStorageService fileStorageService;

    // 1. KAYIT OL
    // Yeni Adres: https://senate.onrender.com/api/register
    @PostMapping("/register")
    public User register(@RequestBody User user) {
        // Kullanıcı adı kontrolü
        if (userRepository.findByUsername(user.getUsername()).isPresent()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Bu kullanıcı adı zaten alınmış.");
        }
        
        // Email kontrolü (Opsiyonel ama önerilir)
        // if (user.getEmail() != null && userRepository.findByEmail(user.getEmail()).isPresent()) {
        //    throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Bu email zaten kullanılıyor.");
        // }

        user.setKarma(0);
        System.out.println("Yeni kullanıcı kaydedildi: " + user.getUsername()); // Loglara yazdıralım
        return userRepository.save(user);
    }

    // 2. GİRİŞ YAP
    // Yeni Adres: https://senate.onrender.com/api/login
    @PostMapping("/login")
    public User login(@RequestBody User loginData) {
        Optional<User> user = userRepository.findByUsername(loginData.getUsername());
        
        if (user.isPresent() && user.get().getPassword().equals(loginData.getPassword())) {
            return user.get();
        }
        throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Hatalı kullanıcı adı veya şifre.");
    }

    // 3. KULLANICI BİLGİSİNİ GETİR
    // Yeni Adres: https://senate.onrender.com/api/{id}
    @GetMapping("/{id}")
    public User getUser(@PathVariable Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Kullanıcı bulunamadı."));
    }

    // 4. TAKİP ET
    @PostMapping("/{targetId}/follow")
    public void followUser(@PathVariable Long targetId, @RequestParam Long currentUserId) {
        User target = userRepository.findById(targetId).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
        User currentUser = userRepository.findById(currentUserId).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));

        if (target.getId().equals(currentUser.getId())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Kendini takip edemezsin.");
        }

        // Listeler null gelirse diye kontrol eklemek iyi olur (NullPointerException önlemek için)
        if (currentUser.getFollowing() != null) currentUser.getFollowing().add(target);
        if (target.getFollowers() != null) target.getFollowers().add(currentUser);
        
        userRepository.save(currentUser);
        userRepository.save(target);
    }

    // 5. TAKİBİ BIRAK
    @PostMapping("/{targetId}/unfollow")
    public void unfollowUser(@PathVariable Long targetId, @RequestParam Long currentUserId) {
        User target = userRepository.findById(targetId).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
        User currentUser = userRepository.findById(currentUserId).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));

        if (currentUser.getFollowing() != null) currentUser.getFollowing().remove(target);
        if (target.getFollowers() != null) target.getFollowers().remove(currentUser);

        userRepository.save(currentUser);
        userRepository.save(target);
    }

    // 6. TAKİP EDİYOR MUYUM?
    @GetMapping("/{targetId}/is-following")
    public boolean isFollowing(@PathVariable Long targetId, @RequestParam Long currentUserId) {
        User target = userRepository.findById(targetId).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
        User currentUser = userRepository.findById(currentUserId).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
        
        return currentUser.getFollowing() != null && currentUser.getFollowing().contains(target);
    }

    // 7. PROFİLİ DÜZENLE (Resim Yüklemeli)
    @PutMapping("/{id}/profile")
    public User updateProfile(
            @PathVariable Long id,
            @RequestParam(required = false) String title,
            @RequestParam(required = false) String bio,
            @RequestParam(required = false) MultipartFile profilePicture,
            @RequestParam(required = false) MultipartFile headerPicture) {

        User user = userRepository.findById(id).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));

        if (title != null) user.setTitle(title);
        if (bio != null) user.setBio(bio);

        // Profil Resmi Yükleme
        if (profilePicture != null && !profilePicture.isEmpty()) {
            String fileName = fileStorageService.storeFile(profilePicture);
            user.setProfilePictureUrl(fileName);
        }

        // Kapak Resmi Yükleme
        if (headerPicture != null && !headerPicture.isEmpty()) {
            String fileName = fileStorageService.storeFile(headerPicture);
            user.setHeaderUrl(fileName);
        }

        return userRepository.save(user);
    }
}