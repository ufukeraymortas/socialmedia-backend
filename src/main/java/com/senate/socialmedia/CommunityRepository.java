package com.senate.socialmedia;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public interface CommunityRepository extends JpaRepository<Community, Long> {
    
    // --- YENİ: EN ESKİ ÜYEYİ BUL (ID'si en küçük olan üye) ---
    @Query(value = "SELECT u.* FROM users u " +
                   "JOIN community_members cm ON u.id = cm.user_id " +
                   "WHERE cm.community_id = :communityId " +
                   "ORDER BY u.id ASC LIMIT 1", nativeQuery = true)
    Optional<User> findOldestMember(Long communityId);
}