package com.senate.socialmedia.controller;

import com.senate.socialmedia.Post;
import com.senate.socialmedia.service.PostService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/api/posts")
@CrossOrigin(origins = "*") 
public class PostController {

    // ARTIK SADECE SERVICE VAR. Repository'leri sildik.
    @Autowired
    private PostService postService;

    // Tüm Postları Getir
    @GetMapping
    public List<Post> getAllPosts() {
        return postService.getAllPosts();
    }
    
    // Kullanıcının Postlarını Getir
    @GetMapping("/user/{userId}")
    public List<Post> getPostsByUser(@PathVariable Long userId) {
        return postService.getPostsByUserId(userId);
    }

    // Yeni Post At (Topluluk, Medya, Alıntı destekli)
    @PostMapping
    public Post createPost(@RequestParam(value = "content", required = false) String content,
                           @RequestParam("authorId") Long authorId,
                           @RequestParam(value = "file", required = false) MultipartFile file,
                           @RequestParam(value = "originalPostId", required = false) Long originalPostId,
                           @RequestParam(value = "communityId", required = false) Long communityId) {
        
        return postService.createPost(content, authorId, file, originalPostId, communityId);
    }
    
    @Autowired
    private com.senate.socialmedia.PostRepository postRepository; // Hızlı erişim için

    @DeleteMapping("/{id}")
    public void deletePost(@PathVariable Long id) {
        // Normalde burada "Silmeye çalışan kişi postun sahibi mi?" kontrolü yapılır.
        // Şimdilik hızlı ilerlemek için direkt siliyoruz.
        postRepository.deleteById(id);
    }
}