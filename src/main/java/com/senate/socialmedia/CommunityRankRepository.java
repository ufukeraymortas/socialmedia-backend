package com.senate.socialmedia;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface CommunityRankRepository extends JpaRepository<CommunityRank, Long> {
    List<CommunityRank> findByCommunityIdOrderByThresholdDesc(Long communityId);
}