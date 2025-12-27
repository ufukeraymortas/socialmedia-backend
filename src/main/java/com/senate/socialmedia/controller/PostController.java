package com.senate.socialmedia.controller;

import com.senate.socialmedia.*;
import com.senate.socialmedia.service.FileStorageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/posts")
@CrossOrigin(origins = "*")
public class PostController {

    @Autowired private PostRepository postRepository;
    @Autowired private UserRepository userRepository;
    @Autowired private CommunityRepository communityRepository;
    @Autowired private VoteRepository voteRepository;
    @Autowired private CommunityRankRepository rankRepository;
    @Autowired private FileStorageService fileStorageService;

    // 1. TÜM POSTLAR
    @GetMapping
    public List<Post> getAllPosts() {
        return postRepository.findAllByOrderByTimestampDesc();
    }

    // 2. KULLANICI POSTLARI
    @GetMapping("/user/{userId}")
    public List<Post> getPostsByUser(@PathVariable Long userId) {
        return postRepository.findByAuthorIdOrderByTimestampDesc(userId);
    }

    // 3. TOPLULUK POSTLARI (Rütbe Hesaplamalı)
    @GetMapping("/community/{communityId}")
    public List<Post> getPostsByCommunity(@PathVariable Long communityId) {
        List<Post> posts = postRepository.findByCommunityIdOrderByTimestampDesc(communityId);
        List<CommunityRank> rules = rankRepository.findByCommunityIdOrderByThresholdDesc(communityId);

        for (Post post : posts) {
            try {
                Integer karma = voteRepository.getUserCommunityKarma(post.getAuthor().getId(), communityId);
                if (karma == null) karma = 0;

                String userRank = ""; 
                for (CommunityRank rule : rules) {
                    if (karma >= rule.getThreshold()) {
                        userRank = rule.getName();
                        break; 
                    }
                }
                post.setAuthorRank(userRank);
            } catch (Exception e) {
                // Hata olursa rütbesiz devam et, akışı bozma
                post.setAuthorRank("");
            }
        }
        return posts;
    }

    // 4. YENİ POST AT
    @PostMapping
    public Post createPost(
            @RequestParam String content,
            @RequestParam Long authorId,
            @RequestParam(required = false) MultipartFile file,
            @RequestParam(required = false) Long originalPostId,
            @RequestParam(required = false) Long communityId) {

        User author = userRepository.findById(authorId).orElseThrow(() -> new RuntimeException("Kullanıcı yok"));
        
        Post post = new Post();
        post.setContent(content);
        post.setAuthor(author);

        if (file != null && !file.isEmpty()) {
            String fileName = fileStorageService.storeFile(file);
            String contentType = file.getContentType();
            if (contentType != null && contentType.startsWith("video")) {
                post.setVideoUrl(fileName);
            } else {
                post.setImageUrl(fileName);
            }
        }

        if (originalPostId != null) {
            post.setOriginalPost(postRepository.findById(originalPostId).orElse(null));
        }

        if (communityId != null) {
            post.setCommunity(communityRepository.findById(communityId).orElse(null));
        }

        return postRepository.save(post);
    }

    // 5. OY VER
    @PostMapping("/{postId}/vote")
    public void vote(@PathVariable Long postId, @RequestParam Long userId, @RequestParam VoteType type) {
        Post post = postRepository.findById(postId).orElseThrow();
        User user = userRepository.findById(userId).orElseThrow();

        Optional<Vote> existingVote = voteRepository.findByPostAndUser(post, user);

        if (existingVote.isPresent()) {
            Vote v = existingVote.get();
            if (v.getType() == type) {
                voteRepository.delete(v);
            } else {
                v.setType(type);
                v.setValue(type == VoteType.UP ? 1 : -1);
                voteRepository.save(v);
            }
        } else {
            Vote newVote = new Vote(type, (type == VoteType.UP ? 1 : -1), user, post);
            voteRepository.save(newVote);
        }
    }

    // 6. POST OY DURUMU
    @GetMapping("/{postId}/vote")
    public VoteResponse getVoteStatus(@PathVariable Long postId, @RequestParam Long userId) {
        Integer score = voteRepository.getPostScore(postId);
        if (score == null) score = 0;

        String userVote = null;
        try {
            Post post = postRepository.findById(postId).orElseThrow();
            User user = userRepository.findById(userId).orElseThrow();
            Optional<Vote> v = voteRepository.findByPostAndUser(post, user);
            if (v.isPresent()) userVote = v.get().getType().toString();
        } catch (Exception e) {}

        return new VoteResponse(score, userVote);
    }

    // 7. SİL
    @DeleteMapping("/{id}")
    public void deletePost(@PathVariable Long id) {
        postRepository.deleteById(id);
    }
}