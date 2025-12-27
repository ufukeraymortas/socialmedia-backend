package com.senate.socialmedia;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public interface ElectionRepository extends JpaRepository<Election, Long> {
    
    // Bir toplulukta şu an devam eden (Aktif) bir seçim var mı?
    Optional<Election> findByCommunityIdAndIsActiveTrue(Long communityId);
}