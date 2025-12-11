package com.senate.socialmedia.service;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import com.senate.socialmedia.Community;
import com.senate.socialmedia.CommunityRepository;
import com.senate.socialmedia.Post;
import com.senate.socialmedia.PostRepository;
import com.senate.socialmedia.User;
import com.senate.socialmedia.UserRepository;

@Service
public class PostService {

    @Autowired
    private PostRepository postRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private FileStorageService fileStorageService;
    
    @Autowired
    private CommunityRepository communityRepository; 

    public List<Post> getAllPosts() {
        return postRepository.findAllByOrderByTimestampDesc();
    }
    
    public List<Post> getPostsByUserId(Long userId) {
        return postRepository.findByAuthorIdOrderByTimestampDesc(userId);
    }

    /**
     * Yeni post, retweet, alıntı veya topluluk gönderisi oluşturur.
     */
    public Post createPost(String content, Long authorId, MultipartFile file, Long originalPostId, Long communityId) {
        
        // 1. Yazarı bul
        User author = userRepository.findById(authorId)
                .orElseThrow(() -> new RuntimeException("Kullanıcı bulunamadı."));

        // 2. Yeni Post nesnesi
        Post newPost = new Post();
        newPost.setContent(content);
        newPost.setAuthor(author);
        newPost.setTimestamp(LocalDateTime.now());
        
        // 3. Retweet veya Alıntı mı?
        if (originalPostId != null) {
            Post original = postRepository.findById(originalPostId)
                    .orElseThrow(() -> new RuntimeException("Orijinal post bulunamadı."));
            newPost.setOriginalPost(original);
        }

        // 4. Topluluk Gönderisi mi? (YENİ)
        if (communityId != null) {
            Community community = communityRepository.findById(communityId)
                    .orElseThrow(() -> new RuntimeException("Topluluk bulunamadı."));
            newPost.setCommunity(community);
        }

        // 5. Medya Kaydetme
        if (file != null && !file.isEmpty()) {
            String fileName = fileStorageService.storeFile(file);
            String fileType = file.getContentType();
            
            if (fileType != null && fileType.startsWith("image")) {
                newPost.setImageUrl(fileName);
            } else if (fileType != null && fileType.startsWith("video")) {
                newPost.setVideoUrl(fileName);
            }
        }

        return postRepository.save(newPost);
    }
}