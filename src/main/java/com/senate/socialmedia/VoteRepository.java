package com.senate.socialmedia;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public interface VoteRepository extends JpaRepository<Vote, Long> {
    
    // Bir kullanıcının bir post'a attığı oyu bul (Daha önce oy vermiş mi?)
    Optional<Vote> findByPostAndUser(Post post, User user);

    // Bir postun toplam puanını hesapla (Up - Down)
    @Query("SELECT SUM(v.value) FROM Vote v WHERE v.post.id = :postId")
    Integer getPostScore(Long postId);

    // --- RÜTBE SİSTEMİ İÇİN ---
    // Kullanıcının belirli bir topluluktaki toplam karması
    @Query("SELECT SUM(v.value) FROM Vote v WHERE v.post.author.id = :userId AND v.post.community.id = :communityId")
    Integer getUserCommunityKarma(Long userId, Long communityId);
}