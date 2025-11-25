package com.senate.socialmedia.service;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.senate.socialmedia.Comment;
import com.senate.socialmedia.CommentRepository;
import com.senate.socialmedia.Post;
import com.senate.socialmedia.PostRepository;
import com.senate.socialmedia.User;
import com.senate.socialmedia.UserRepository;

@Service
public class CommentService {

    // Yorumu kaydetmek için buna ihtiyacımız var
    @Autowired
    private CommentRepository commentRepository;

    // Yorumu hangi posta ekleyeceğimizi bulmak için buna ihtiyacımız var
    @Autowired
    private PostRepository postRepository;

    // Yorumu kimin attığını bulmak için buna ihtiyacımız var
    @Autowired
    private UserRepository userRepository;

    // Fotoğraf/video kaydetmek için motorumuza ihtiyacımız var
    @Autowired
    private FileStorageService fileStorageService;


    /**
     * Belirli bir posta ait tüm yorumları getirir.
     */
 
    public List<Comment> getCommentsForPost(Long postId) {
    		
        	Post post = postRepository.findById(postId)
        			.orElseThrow(() -> new RuntimeException("Post bulunamadı, id: " + postId));
        
        	return commentRepository.findByPostAndParentCommentIsNullOrderByTimestampAsc(post);
    }
    
    public List<Comment> getCommentsByUser(Long userId) {
        User author = userRepository.findById(userId)
             .orElseThrow(() -> new RuntimeException("Kullanıcı bulunamadı."));
        // Repository'de findByAuthorOrderByTimestampDesc metodunun olması gerekir
        return commentRepository.findByAuthorOrderByTimestampDesc(author);
    }
 

    public Comment createComment(String content, Long authorId, Long postId, MultipartFile file, Long parentCommentId) {

        // 1. Yorumu atan kullanıcıyı (User) ID ile bul (Aynı)
        User author = userRepository.findById(authorId)
                .orElseThrow(() -> new RuntimeException("Kullanıcı bulunamadı, id: " + authorId));

        // 2. Yorumun atıldığı postu (Post) ID ile bul (Aynı)
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new RuntimeException("Post bulunamadı, id: " + postId));

        // 3. Yeni, boş bir Comment nesnesi oluştur (Aynı)
        Comment newComment = new Comment();
        newComment.setContent(content);
        newComment.setAuthor(author);
        newComment.setPost(post);
        newComment.setTimestamp(LocalDateTime.now());

        // 4. YENİ MANTIK: Bu bir cevap mı?
        if (parentCommentId != null) {
            // Eğer bir 'parentCommentId' (ana yorum ID'si) geldiyse...
            // O ana yorumu veritabanından bul
            Comment parentComment = commentRepository.findById(parentCommentId)
                    .orElseThrow(() -> new RuntimeException("Ana yorum bulunamadı, id: " + parentCommentId));
            // Yeni yorumun "anası" olarak onu ayarla
            newComment.setParentComment(parentComment);
        }
        // (Eğer parentCommentId 'null' ise, bu alan boş kalır, yani bu bir ana yorumdur)

        // 5. FOTOĞRAF/VİDEO MANTIĞI (Aynı)
        if (file != null && !file.isEmpty()) {
            String fileName = fileStorageService.storeFile(file);
            String fileType = file.getContentType();

            if (fileType != null && fileType.startsWith("image")) {
                newComment.setImageUrl(fileName);
            } else if (fileType != null && fileType.startsWith("video")) {
                newComment.setVideoUrl(fileName);
            }
        }

        // 6. Her şeyi ayarlanmış olan bu yeni yorumu/cevabı veritabanına kaydet (Aynı)
        return commentRepository.save(newComment);
    }
}