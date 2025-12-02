package com.senate.socialmedia;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public interface LikeRepository extends JpaRepository<Like, Long> {

    // 1. Bu kullanıcı, bu postu DAHA ÖNCE beğendi/oyladı mı?
    Optional<Like> findByUserIdAndPostId(Long userId, Long postId);
    
    // 2. Sadece belirli oyları say (Yeni Eklediğimiz)
    long countByPostAndVoteType(Post post, VoteType voteType);
    
    // 3. Silme işlemi
    void deleteByUserIdAndPostId(Long userId, Long postId);
}