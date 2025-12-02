package com.senate.socialmedia.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.HashMap;

import com.senate.socialmedia.service.LikeService;
import com.senate.socialmedia.VoteType;

@RestController
@RequestMapping("/api/posts/{postId}/vote") // Adresi '/vote' olarak değiştirdik
public class LikeController {

    @Autowired
    private LikeService likeService;

    /**
     * Oy Ver (Up veya Down)
     * POST /api/posts/5/vote?userId=1&type=UP
     */
    @PostMapping
    public Map<String, Object> vote(@PathVariable Long postId, 
                                    @RequestParam Long userId,
                                    @RequestParam VoteType type) { // 'type' parametresi UP veya DOWN olacak
        
        likeService.vote(postId, userId, type);
        
        // İşlemden sonra güncel puanı dön
        long newScore = likeService.getPostScore(postId);
        
        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("newScore", newScore);
        return response;
    }

    /**
     * Puanı ve kullanıcının durumunu getir
     * GET /api/posts/5/vote?userId=1
     */
    @GetMapping
    public Map<String, Object> getVoteStatus(@PathVariable Long postId, @RequestParam Long userId) {
        
        long score = likeService.getPostScore(postId);
        VoteType userVote = likeService.getUserVoteType(postId, userId); // UP, DOWN veya null
        
        Map<String, Object> response = new HashMap<>();
        response.put("score", score);
        response.put("userVote", userVote); // Frontend bunu kullanarak butonu boyayacak
        return response;
    }
}