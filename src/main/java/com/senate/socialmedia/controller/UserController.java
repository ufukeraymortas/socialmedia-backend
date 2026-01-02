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

@RestController
@RequestMapping("/api/users")
@CrossOrigin(origins = "*") 
public class UserController {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private FileStorageService fileStorageService; // Resim yükleme için

    // 1. KAYIT OL
    @PostMapping("/register")
    public User register(@RequestBody User user) {
        if (userRepository.findByUsername(user.getUsername()).isPresent()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Bu kullanıcı adı zaten alınmış.");
        }
        user.setKarma(0);
        return userRepository.save(user);
    }

    // 2. GİRİŞ YAP
    @PostMapping("/login")
    public User login(@RequestBody User loginData) {
        Optional<User> user = userRepository.findByUsername(loginData.getUsername());
        if (user.isPresent() && user.get().getPassword().equals(loginData.getPassword())) {
            return user.get();
        }
        throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Hatalı kullanıcı adı veya şifre.");
    }

    // 3. KULLANICI BİLGİSİNİ GETİR
    @GetMapping("/{id}")
    public User getUser(@PathVariable Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Kullanıcı bulunamadı."));
    }

    // 4. TAKİP ET
    @PostMapping("/{targetId}/follow")
    public void followUser(@PathVariable Long targetId, @RequestParam Long currentUserId) {
        User target = userRepository.findById(targetId).orElseThrow();
        User currentUser = userRepository.findById(currentUserId).orElseThrow();

        if (target.getId().equals(currentUser.getId())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Kendini takip edemezsin.");
        }

        currentUser.getFollowing().add(target); // Ben onu takip ediyorum
        target.getFollowers().add(currentUser); // O beni takipçi olarak kazanıyor
        
        userRepository.save(currentUser);
        userRepository.save(target);
    }

    // 5. TAKİBİ BIRAK
    @PostMapping("/{targetId}/unfollow")
    public void unfollowUser(@PathVariable Long targetId, @RequestParam Long currentUserId) {
        User target = userRepository.findById(targetId).orElseThrow();
        User currentUser = userRepository.findById(currentUserId).orElseThrow();

        currentUser.getFollowing().remove(target);
        target.getFollowers().remove(currentUser);

        userRepository.save(currentUser);
        userRepository.save(target);
    }

    // 6. TAKİP EDİYOR MUYUM?
    @GetMapping("/{targetId}/is-following")
    public boolean isFollowing(@PathVariable Long targetId, @RequestParam Long currentUserId) {
        User target = userRepository.findById(targetId).orElseThrow();
        User currentUser = userRepository.findById(currentUserId).orElseThrow();
        return currentUser.getFollowing().contains(target);
    }

    // 7. PROFİLİ DÜZENLE (Resim Yüklemeli)
    @PutMapping("/{id}/profile")
    public User updateProfile(
            @PathVariable Long id,
            @RequestParam(required = false) String title,
            @RequestParam(required = false) String bio,
            @RequestParam(required = false) MultipartFile profilePicture,
            @RequestParam(required = false) MultipartFile headerPicture) {

        User user = userRepository.findById(id).orElseThrow();

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