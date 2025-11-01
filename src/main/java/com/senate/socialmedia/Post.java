package com.senate.socialmedia;

import java.time.LocalDateTime; // Gönderi zamanı için
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "posts")
public class Post {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String content; // Gönderinin içeriği (örn: "Merhaba dünya!")

    private LocalDateTime timestamp; // Gönderinin atıldığı zaman

   
    @ManyToOne(fetch = FetchType.EAGER) // (Performans için 'LAZY' kullanılır)
    @JoinColumn(name = "author_id") // Veritabanındaki yabancı anahtar sütununun adı
    private User author; // Gönderiyi kimin yazdığı bilgisi
 // ... (mevcut timestamp ve author alanları)

 // YENİ ALAN: Yüklenen fotoğrafın sunucudaki yolunu
 // (örn: /uploads/resim123.jpg) saklamak için.
    private String imageUrl;
    private String videoUrl;

    public String getVideoUrl() {
		return videoUrl;
	}

	public void setVideoUrl(String videoUrl) {
		this.videoUrl = videoUrl;
	}

	public String getImageUrl() {
		return imageUrl;
	}

	public void setImageUrl(String imageUrl) {
		this.imageUrl = imageUrl;
	}

    public Post() {
    }
    
    // --- Getter ve Setter Metotları ---
    // Yine sağ tıklayın -> Source -> Generate Getters and Setters... -> Select All -> Generate

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(LocalDateTime timestamp) {
        this.timestamp = timestamp;
    }

    public User getAuthor() {
        return author;
    }

    public void setAuthor(User author) {
        this.author = author;
    }
}