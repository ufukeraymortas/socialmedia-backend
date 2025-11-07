package com.senate.socialmedia;

import jakarta.persistence.*; // JPA Annotations (Etiketleri) için

@Entity
@Table(name = "post_likes") // Veritabanındaki tablonun adı
public class Like {
	
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    
    @ManyToOne
    @JoinColumn(name = "user_id") // Bu sütun, 'users' tablosuna bağlanır
    private User user;
    
    @ManyToOne
    @JoinColumn(name = "post_id") 
    private Post post;
    
    
    // JPA için gerekli boş constructor
    public Like() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public Post getPost() {
        return post;
    }

    public void setPost(Post post) {
        this.post = post;
    }
}