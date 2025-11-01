package com.senate.socialmedia.controller;

import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.senate.socialmedia.User;
import com.senate.socialmedia.dto.RegisterRequest; // Birazdan bu sınıfı oluşturacağız
import com.senate.socialmedia.service.UserService;
import com.senate.socialmedia.dto.LoginRequest;
@RestController // 1. Bunun bir REST API Controller olduğunu söyler
@RequestMapping("/api/users") // 2. Bu sınıftaki tüm adresler /api/users ile başlar
public class UserController {

    @Autowired // 3. UserService'i buraya enjekte et
    private UserService userService;

    // 4. /api/users/register adresine gelen bir HTTP POST isteğini yakalar
    @PostMapping("/register")
    public User registerUser(@RequestBody RegisterRequest request) {
        // 5. @RequestBody, gelen JSON verisini 'RegisterRequest' nesnesine çevirir.
        //    (örn: { "username": "ahmet", "password": "123" } )
        
        // 6. İşi yapması için Servis katmanını çağır
        return userService.registerUser(request.getUsername(), request.getPassword());
    }
    @PostMapping("/login") 
    
    // YENİ METODUMUZ (ZİLİN BAĞLANDIĞI YER):
    public User loginUser(@RequestBody LoginRequest loginRequest) {
        
        // Bu satır, asıl işi (şifre kontrolünü) yapması için UserService'i çağırır
        return userService.loginUser(loginRequest.getUsername(), loginRequest.getPassword());
    }

} // Bu, sınıfın en son kapanış parantezidir
    // (Daha sonra buraya /login endpoint'i de eklenecek)