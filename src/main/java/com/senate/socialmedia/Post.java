package com.senate.socialmedia;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;

@Entity
@Table(name = "posts")
public class Post {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String content; 
    
    private LocalDateTime timestamp;
    
    private String imageUrl; // Fotoğraf adresi
    
    private String videoUrl; // Video adresi

    // İlişki 1: Kim attı? (Post'tan User'a)
    @ManyToOne(fetch = FetchType.EAGER) // Postu çekerken yazarı hemen getir
    @JoinColumn(name = "author_id") 
    private User author;

    // İlişki 2: Beğeniler (Post'tan Like'a)
    // Post silinirse, ona ait tüm beğeniler de silinsin (cascade)
    @OneToMany(mappedBy = "post", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonIgnore // JSON sonsuz döngüsünü engellemek için
    private Set<Like> likes = new HashSet<>(); 

    // İlişki 3: Yorumlar (Post'tan Comment'a)
    @OneToMany(mappedBy = "post", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonIgnore // JSON sonsuz döngüsünü engellemek için
    private Set<Comment> comments;

    // JPA için gerekli boş constructor
    public Post() {
    }
    
    // --- Getter ve Setter Metotları ---

    public Long getId() {
        return id;
    }
    // ... (Diğer Get/Set metotlarını ekleyin veya otomatik oluşturun)

    public void setContent(String content) {
        this.content = content;
    }
    
    // Yazar (author) için Getter ve Setter'ları burada olmalıdır.
    // Likes ve Comments için de Getter ve Setter'lar burada olmalıdır.
    
    public Set<Comment> getComments() {
		return comments;
	}

	public void setComments(Set<Comment> comments) {
		this.comments = comments;
	}

	public String getContent() {
		return content;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public User getAuthor() {
        return author;
    }

    public void setAuthor(User author) {
        this.author = author;
    }

    public Set<Like> getLikes() {
        return likes;
    }

    public void setLikes(Set<Like> likes) {
        this.likes = likes;
    }

	public LocalDateTime getTimestamp() {
		return timestamp;
	}

	public void setTimestamp(LocalDateTime timestamp) {
		this.timestamp = timestamp;
	}

	public String getImageUrl() {
		return imageUrl;
	}

	public void setImageUrl(String imageUrl) {
		this.imageUrl = imageUrl;
	}

	public String getVideoUrl() {
		return videoUrl;
	}

	public void setVideoUrl(String videoUrl) {
		this.videoUrl = videoUrl;
	}
    
    
    // ... (Diğer tüm alanlar için getter ve setter'lar olmalıdır)
}