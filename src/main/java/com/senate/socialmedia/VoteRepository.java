package com.senate.socialmedia;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface VoteRepository extends JpaRepository<Vote, Long> {
    
    Optional<Vote> findByPostAndUser(Post post, User user);

    @Query("SELECT SUM(v.value) FROM Vote v WHERE v.post.id = :postId")
    Integer getPostScore(Long postId);

    @Query("SELECT SUM(v.value) FROM Vote v WHERE v.post.author.id = :userId AND v.post.community.id = :communityId")
    Integer getUserCommunityKarma(Long userId, Long communityId);

    // --- YENİ: TOPLULUKTAKİ EN YÜKSEK KARMALI KİŞİLERİ GETİR ---
    // (Adaylık kontrolü için)
    @Query("SELECT p.author FROM Post p JOIN Vote v ON v.post = p " +
           "WHERE p.community.id = :communityId " +
           "GROUP BY p.author " +
           "ORDER BY SUM(v.value) DESC")
    List<User> findTopKarmaUsers(Long communityId);
}