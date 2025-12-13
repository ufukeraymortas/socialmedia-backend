package com.senate.socialmedia.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import com.senate.socialmedia.User;
import com.senate.socialmedia.dto.LoginRequest;
import com.senate.socialmedia.dto.RegisterRequest;
import com.senate.socialmedia.service.UserService;

// Tüm API'ler /api/users ile başlayacak
@RestController
@RequestMapping("/api/users")
public class UserController {

    @Autowired
    private UserService userService;

    // --------------------------------------------------
    // A. KAYIT ve GİRİŞ API'LERİ
    // --------------------------------------------------

    // POST /api/users/register (Kayıt Ol)
    @PostMapping("/register")
    public User register(@RequestBody RegisterRequest request) {
        return userService.registerUser(request.getUsername(), request.getPassword());
    }
    
    // POST /api/users/login (Giriş Yap)
    @PostMapping("/login")
    public User loginUser(@RequestBody LoginRequest loginRequest) {
        return userService.loginUser(loginRequest.getUsername(), loginRequest.getPassword());
    }

    // --------------------------------------------------
    // B. PROFİL API'LERİ (YENİ EKLENENLER)
    // --------------------------------------------------
    
    // GET /api/users/{userId} (Kullanıcının profilini ID ile getir)
    @GetMapping("/{userId}")
    public User getUserProfile(@PathVariable Long userId) {
        return userService.findById(userId);
    }

 // UserController.java içindeki updateProfile metodu (GÜNCELLENMİŞ HALİ):
    @PutMapping("/{id}/profile")
    public User updateProfile(@PathVariable Long id, 
                              @RequestParam(required=false) String title,
                              @RequestParam(required=false) String bio,
                              @RequestParam(value="profilePicture", required=false) MultipartFile profilePicture,
                              @RequestParam(value="headerPicture", required=false) MultipartFile headerPicture) { // YENİ
        
        return userService.updateUserProfile(id, title, bio, profilePicture, headerPicture);
    }
 
    // Takip Et
    @PostMapping("/{userId}/follow")
    public void followUser(@PathVariable Long userId, @RequestParam Long currentUserId) {
        userService.followUser(currentUserId, userId);
    }

    // Takibi Bırak
    @PostMapping("/{userId}/unfollow")
    public void unfollowUser(@PathVariable Long userId, @RequestParam Long currentUserId) {
        userService.unfollowUser(currentUserId, userId);
    }

    // Takip Durumunu Kontrol Et (Butonun rengi için lazım)
    @GetMapping("/{userId}/is-following")
    public boolean isFollowing(@PathVariable Long userId, @RequestParam Long currentUserId) {
        return userService.isFollowing(currentUserId, userId);
    }
}