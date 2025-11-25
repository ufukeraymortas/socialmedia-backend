package com.senate.socialmedia;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CommentRepository extends JpaRepository<Comment, Long> {

    // JpaRepository<Comment, Long> sayesinde:
    // save(), findById(), findAll(), delete() metotları OTOMATİK olarak geldi!

    // --- Kendi Özel Metotlarımızı Ekleyelim ---

	// "FindByPost... AND ParentCommentIsNull... OrderByTimestampAsc"
	// (Post'a göre bul... VE Ana Yorumu Boş olanları... Sırala)
	List<Comment> findByPostAndParentCommentIsNullOrderByTimestampAsc(Post post);
    
	List<Comment> findByAuthorOrderByTimestampDesc(User author);
    // 2. (Alternatif) Belirli bir posta ait tüm yorumları bul
    // (Zaman damgasına göre azalan sırada - en YENİ yorum en üstte)
    // List<Comment> findByPostOrderByTimestampDesc(Post post);

}