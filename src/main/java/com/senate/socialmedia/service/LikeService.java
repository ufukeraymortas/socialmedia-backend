package com.senate.socialmedia.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

// Gerekli importlar (kütüphane çağrıları)
import com.senate.socialmedia.Like;
import com.senate.socialmedia.LikeRepository;
import com.senate.socialmedia.Post;
import com.senate.socialmedia.User;
import com.senate.socialmedia.PostRepository;
import com.senate.socialmedia.UserRepository;

import java.util.Optional; // Hata veren Optional sınıfı için

@Service
public class LikeService {

    @Autowired
    private LikeRepository likeRepository;

    @Autowired
    private PostRepository postRepository;

    @Autowired
    private UserRepository userRepository;

    /**
     * Bir postu beğenme/beğeniyi kaldırma işlemini yönetir (toggle).
     * @return true ise yeni beğeni eklendi, false ise mevcut beğeni kaldırıldı.
     */
    public boolean toggleLike(Long postId, Long userId) {
        
        // 1. Kullanıcı ve Post'un varlığını kontrol et
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("Kullanıcı bulunamadı."));
        
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new RuntimeException("Post bulunamadı."));

        // 2. Bu kullanıcının bu postu zaten beğenip beğenmediğini kontrol et
        // findByUserIdAndPostId metodu LikeRepository'de tanımlı.
        Optional<Like> existingLike = likeRepository.findByUserIdAndPostId(userId, postId);

        if (existingLike.isPresent()) {
            // 3. Zaten beğendiyse: Beğeniyi Kaldır (Delete)
            // existingLike.get() ile Optional içindeki Like nesnesini alıyoruz.
            likeRepository.delete(existingLike.get()); 
            return false; // Beğeni kaldırıldı
        } else {
            // 4. Beğenmediyse: Yeni Beğeni Ekle (Save)
            Like newLike = new Like();
            newLike.setUser(user);    // <-- setUser metodu Like.java'da tanımlı
            newLike.setPost(post);    // <-- setPost metodu Like.java'da tanımlı
            likeRepository.save(newLike);
            return true; // Yeni beğeni eklendi
        }
    }
    
    /**
     * Bir postun toplam beğeni sayısını döner.
     */
    public long getLikeCount(Long postId) {
        // countByPostId metodu LikeRepository'de tanımlı.
        return likeRepository.countByPostId(postId);
    }

    /**
     * Kullanıcının belirli bir postu beğenip beğenmediğini kontrol eder.
     */
    public boolean isLikedByUser(Long postId, Long userId) {
        return likeRepository.findByUserIdAndPostId(userId, postId).isPresent();
    }
}