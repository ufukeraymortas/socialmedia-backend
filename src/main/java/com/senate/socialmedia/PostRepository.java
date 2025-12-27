package com.senate.socialmedia;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;

@Repository
public interface PostRepository extends JpaRepository<Post, Long> {

    // 1. Ana Akış İçin: Tüm postları yeniden eskiye sırala
    List<Post> findAllByOrderByTimestampDesc();

    // 2. Profil Sayfası İçin: Sadece belirli bir yazarın postlarını getir (EKSİK OLAN BUYDU)
    List<Post> findByAuthorIdOrderByTimestampDesc(Long authorId);
    
    // Belirli bir topluluğun postlarını getir (Yeniden eskiye)
    List<Post> findByCommunityIdOrderByTimestampDesc(Long communityId);
    
    // Bir topluluğa ait tüm postları sil
    @Transactional // Silme işlemi hassas olduğu için bu onay şart
    void deleteByCommunityId(Long communityId);
}