package com.senate.socialmedia;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public interface LikeRepository extends JpaRepository<Like, Long> {

    // --- ÖZEL METOTLARIMIZ ---
    
    // 1. Bu kullanıcı, bu postu DAHA ÖNCE beğendi mi? (Tekrar beğenmesini engeller)
    Optional<Like> findByUserIdAndPostId(Long userId, Long postId);
    
    // 2. Bu postu kaç kişi beğendi? (Beğeni sayısını bulur)
    long countByPostId(Long postId);
    
    // 3. Beğeniyi geri almak için o 'Like' nesnesini bulur
    void deleteByUserIdAndPostId(Long userId, Long postId);
}