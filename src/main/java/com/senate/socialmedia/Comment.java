package com.senate.socialmedia;

import java.time.LocalDateTime;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;

@Entity
@Table(name = "comments") // Veritabanındaki tablo adı 'comments' olacak
public class Comment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String content; // Yorumun metni
    private LocalDateTime timestamp; // Yorumun zaman damgası
    
    // Yorumdaki medya (Post'taki gibi)
    private String imageUrl;
    private String videoUrl;

    // --- İLİŞKİLER (ÇOK ÖNEMLİ) ---

    // 1. İlişki: Yorumu kim attı?
    // Birçok (Many) Yorum, Bir (One) Kullanıcıya aittir
    @ManyToOne(fetch = FetchType.EAGER) // 'EAGER' yapıyoruz ki JSON hatası almayalım
    @JoinColumn(name = "author_id") // 'users' tablosuna bağlanan anahtar
    private User author;

    // 2. İlişki: Yorum hangi posta atıldı?
    // Birçok (Many) Yorum, Bir (One) Posta aittir
    @ManyToOne(fetch = FetchType.LAZY) // Bunu 'LAZY' bırakabiliriz
    @JoinColumn(name = "post_id") // 'posts' tablosuna bağlanan anahtar
    private Post post;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "parent_comment_id") // Veritabanındaki yeni sütun adı
    private Comment parentComment;

    // 2. Bu yoruma gelen "Cevapların Listesi"=
    // Bir (One) Ana Yorumun, Birçok (Many) Cevabı olabilir
    @OneToMany(mappedBy = "parentComment", fetch = FetchType.LAZY)
    private List<Comment> replies;

    public Comment() {
    }
 
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

    public User getAuthor() {
        return author;
    }

    public void setAuthor(User author) {
        this.author = author;
    }
    @JsonIgnore
    public Post getPost() {
        return post;
    }
    @JsonIgnore
    public Comment getParentComment() {
		return parentComment;
	}

	public void setParentComment(Comment parentComment) {
		this.parentComment = parentComment;
	}

	public List<Comment> getReplies() {
		return replies;
	}

	public void setReplies(List<Comment> replies) {
		this.replies = replies;
	}

	public void setPost(Post post) {
        this.post = post;
    }
}