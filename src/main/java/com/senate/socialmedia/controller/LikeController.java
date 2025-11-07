package com.senate.socialmedia.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.HashMap;

import com.senate.socialmedia.service.LikeService;

@RestController
@RequestMapping("/api/posts/{postId}/likes") // Post bazlı API'ler
public class LikeController {

    @Autowired
    private LikeService likeService;

    /**
     * Beğeni ekleme/kaldırma işlemi için API (toggle).
     * POST /api/posts/{postId}/likes?userId=123
     */
    @PostMapping
    public Map<String, Boolean> toggleLike(@PathVariable Long postId, @RequestParam Long userId) {
        
        boolean isLiked = likeService.toggleLike(postId, userId);
        
        // Frontend'e yeni durumu ve beğeni sayısını döndür
        Map<String, Boolean> response = new HashMap<>();
        response.put("isLiked", isLiked);
        return response;
    }

    /**
     * Beğeni sayısını ve kullanıcının beğenip beğenmediğini döndürür.
     * GET /api/posts/{postId}/likes?userId=123
     */
    @GetMapping
    public Map<String, Object> getLikeStatus(@PathVariable Long postId, @RequestParam Long userId) {
        
        long count = likeService.getLikeCount(postId);
        boolean isLiked = likeService.isLikedByUser(postId, userId);
        
        Map<String, Object> response = new HashMap<>();
        response.put("count", count);
        response.put("isLiked", isLiked);
        return response;
    }
}