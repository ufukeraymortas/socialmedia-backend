package com.senate.socialmedia.service;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.senate.socialmedia.Post;
import com.senate.socialmedia.PostRepository;
import com.senate.socialmedia.User;
import com.senate.socialmedia.UserRepository; // Gönderiyi atan kullanıcıyı bulmak için bu da lazım

@Service
public class PostService {

    @Autowired
    private PostRepository postRepository;

    @Autowired
    private UserRepository userRepository; // Gönderiyi atan kullanıcıyı bulmak için
    
    @Autowired
    private FileStorageService fileStorageService;
    
    @Autowired
    private com.senate.socialmedia.CommunityRepository communityRepository;

    /**
     * Ana sayfa akışı için tüm gönderileri getiren iş mantığı.
     */
    public List<Post> getAllPosts() {
        // Repository'de tanımladığımız özel metodu kullanıyoruz
        return postRepository.findAllByOrderByTimestampDesc();
    }

    /**
     * Yeni bir gönderi oluşturan iş mantığı.
     */
    /**
     * Yeni post, retweet veya alıntı oluşturur.
     */
    public Post createPost(String content, Long authorId, MultipartFile file, Long originalPostId, Long communityId) {
        
        User author = userRepository.findById(authorId)
                .orElseThrow(() -> new RuntimeException("Kullanıcı bulunamadı."));

        Post newPost = new Post();
        newPost.setContent(content);
        newPost.setAuthor(author);
        newPost.setTimestamp(java.time.LocalDateTime.now());
        
        // Retweet/Alıntı Kontrolü
        if (originalPostId != null) {
            Post original = postRepository.findById(originalPostId)
                    .orElseThrow(() -> new RuntimeException("Orijinal post bulunamadı."));
            newPost.setOriginalPost(original);
        }

        // YENİ: Topluluk Kontrolü
        if (communityId != null) {
            Community community = communityRepository.findById(communityId)
                    .orElseThrow(() -> new RuntimeException("Topluluk bulunamadı."));
            newPost.setCommunity(community);
        }

        // Medya İşlemleri (Aynı kalıyor)
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