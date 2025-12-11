package com.senate.socialmedia.controller;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.senate.socialmedia.Post;
import com.senate.socialmedia.PostRepository;
import com.senate.socialmedia.User;
import com.senate.socialmedia.UserRepository;
import com.senate.socialmedia.service.PostService;

@RestController
@RequestMapping("/api/posts") // Bu sınıftaki tüm adresler /api/posts ile başlar
public class PostController {

    @Autowired
    private PostService postService;
    
    @Autowired
    private UserRepository userRepository; 
    
    @Autowired
    private PostRepository postRepository; 
    
    // /api/posts adresine gelen bir HTTP GET isteğini yakalar
    // Bu, ana sayfa akışıdır
    @GetMapping
    public List<Post> getAllPosts() {
        // Servisi çağır
        return postService.getAllPosts();
    }

    // /api/posts adresine gelen bir HTTP POST isteğini yakalar
    // Bu, yeni gönderi oluşturma işlemidir
    @PostMapping
    public Post createPost(@RequestParam(value = "content", required = false) String content,
                           @RequestParam("authorId") Long authorId,
                           @RequestParam(value = "file", required = false) MultipartFile file,
                           @RequestParam(value = "originalPostId", required = false) Long originalPostId,
                           // YENİ PARAMETRE:
                           @RequestParam(value = "communityId", required = false) Long communityId) {
        
        // Servise communityId'yi de gönderiyoruz
        return postService.createPost(content, authorId, file, originalPostId, communityId);
    }

    // GET /api/posts/user/{userId} -> Bir kullanıcının postlarını getir
    @GetMapping("/user/{userId}")
    public List<Post> getPostsByUser(@PathVariable Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("Kullanıcı bulunamadı."));
        
        // Repository'deki metodu çağır
        return postRepository.findByAuthorOrderByTimestampDesc(user);
    }
}
