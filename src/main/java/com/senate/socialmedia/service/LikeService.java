package com.senate.socialmedia.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.senate.socialmedia.Like;
import com.senate.socialmedia.LikeRepository;
import com.senate.socialmedia.Post;
import com.senate.socialmedia.PostRepository;
import com.senate.socialmedia.User;
import com.senate.socialmedia.UserRepository;
import com.senate.socialmedia.VoteType; // Enum'ımızı ekledik

import java.util.Optional;

@Service
public class LikeService {

    @Autowired
    private LikeRepository likeRepository;
    @Autowired
    private PostRepository postRepository;
    @Autowired
    private UserRepository userRepository;

    /**
     * Oy verme işlemi (Upvote veya Downvote).
     * Mantık: Aynı oya tekrar basarsa siler, farklıya basarsa değiştirir.
     */
    public void vote(Long postId, Long userId, VoteType voteType) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("Kullanıcı bulunamadı."));
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new RuntimeException("Post bulunamadı."));

        // Kullanıcı daha önce oy vermiş mi?
        Optional<Like> existingLike = likeRepository.findByUserIdAndPostId(userId, postId);

        if (existingLike.isPresent()) {
            Like like = existingLike.get();
            
            // SENARYO 1: Kullanıcı ZATEN aynı oyu vermiş (Örn: UP iken tekrar UP'a bastı)
            if (like.getVoteType() == voteType) {
                likeRepository.delete(like); // Oyu geri al (Sil)
            } 
            // SENARYO 2: Kullanıcı fikrini değiştirmiş (Örn: UP iken DOWN'a bastı)
            else {
                like.setVoteType(voteType); // Türü değiştir
                likeRepository.save(like);  // Güncelle
            }
        } else {
            // SENARYO 3: İlk defa oy veriyor
            Like newLike = new Like();
            newLike.setUser(user);
            newLike.setPost(post);
            newLike.setVoteType(voteType); // UP mı DOWN mı olduğunu kaydet
            likeRepository.save(newLike);
        }
    }
    
    /**
     * Reddit Puanını Hesapla: (Upvote Sayısı - Downvote Sayısı)
     */
    public long getPostScore(Long postId) {
        Post post = postRepository.findById(postId).orElseThrow();
        
        long upvotes = likeRepository.countByPostAndVoteType(post, VoteType.UP);
        long downvotes = likeRepository.countByPostAndVoteType(post, VoteType.DOWN);
        
        return upvotes - downvotes; // Sonuç eksi olabilir (Örn: -5)
    }

    /**
     * Kullanıcının şu anki durumu nedir? (UP mı verdi, DOWN mu, yoksa HİÇBİRİ mi?)
     * Frontend'de butonu renkli yapmak için lazım.
     */
    public VoteType getUserVoteType(Long postId, Long userId) {
        return likeRepository.findByUserIdAndPostId(userId, postId)
                .map(Like::getVoteType)
                .orElse(null); // Hiç oy vermemişse null döner
    }
}