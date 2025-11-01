package com.senate.socialmedia.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import com.senate.socialmedia.Post;
import com.senate.socialmedia.service.PostService;

@RestController
@RequestMapping("/api/posts") // Bu sınıftaki tüm adresler /api/posts ile başlar
public class PostController {

    @Autowired
    private PostService postService;

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
    public Post createPost(@RequestParam("content") String content,
                           @RequestParam("authorId") Long authorId,
                           @RequestParam(value = "file", required = false) MultipartFile file) {

        // Gelen parçaları (content, authorId ve opsiyonel dosya)
        // doğrudan güncellediğimiz PostService'e paslıyoruz.
        return postService.createPost(content, authorId, file);
    }
}
