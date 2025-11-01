package com.senate.socialmedia.service;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.senate.socialmedia.Post;
import com.senate.socialmedia.PostRepository;
import com.senate.socialmedia.User;
import com.senate.socialmedia.UserRepository; // Gönderiyi atan kullanıcıyı bulmak için bu da lazım

@Service
public class PostService {

    @Autowired
    private PostRepository postRepository;

    @Autowired
    private UserRepository userRepository; // Gönderiyi atan kullanıcıyı bulmak için
    
    @Autowired
    private FileStorageService fileStorageService;

    /**
     * Ana sayfa akışı için tüm gönderileri getiren iş mantığı.
     */
    public List<Post> getAllPosts() {
        // Repository'de tanımladığımız özel metodu kullanıyoruz
        return postRepository.findAllByOrderByTimestampDesc();
    }

    /**
     * Yeni bir gönderi oluşturan iş mantığı.
     */
    public Post createPost(String content, Long authorId, MultipartFile file) {

        // 1. Gönderiyi atan kullanıcıyı (User) ID ile veritabanından bul
        User author = userRepository.findById(authorId)
                .orElseThrow(() -> new RuntimeException("Kullanıcı bulunamadı, id: " + authorId));

        // 2. Yeni Post nesnesini oluştur
        Post newPost = new Post();
        newPost.setContent(content);
        newPost.setAuthor(author);
        newPost.setTimestamp(LocalDateTime.now());

        // 3. YENİ FOTOĞRAF MANTIĞI
        // Eğer bir dosya yüklendiyse (ve boş değilse)
     // ...
     // 3. YENİ FOTOĞRAF/VİDEO MANTIĞI
     if (file != null && !file.isEmpty()) {
         // Dosyayı FileStorageService kullanarak diske kaydet
         // (Bu servis hem fotoğraf hem videoyu kaydedebilir, o jeneriktir)
         String fileName = fileStorageService.storeFile(file);

         // Gelen dosyanın türünü (MIME type) kontrol et
         String fileType = file.getContentType();

         if (fileType != null && fileType.startsWith("image")) {
             // Eğer türü 'image/jpeg', 'image/png' ise:
             newPost.setImageUrl(fileName);
         } else if (fileType != null && fileType.startsWith("video")) {
             // Eğer türü 'video/mp4', 'video/webm' ise:
             newPost.setVideoUrl(fileName);
         }
     }
     // ...
        // (Eğer dosya yoksa, imageUrl alanı 'null' olarak kalır, bu normal)

        // 4. Veritabanına kaydet
        return postRepository.save(newPost);
    }
}