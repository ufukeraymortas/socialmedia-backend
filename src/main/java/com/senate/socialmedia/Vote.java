package com.senate.socialmedia;

import jakarta.persistence.*;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.time.LocalDateTime; // Artık bunu kullanacağız!

@Entity
@Table(name = "votes")
public class Vote {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    private VoteType type;

    private int value; 

    private LocalDateTime timestamp; // <--- EKLENDİ

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    @JsonIgnoreProperties({"votes", "posts", "following", "followers", "password"}) 
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "post_id", nullable = false)
    @JsonIgnoreProperties({"votes", "author", "community"})
    private Post post;

    public Vote() {}

    public Vote(VoteType type, int value, User user, Post post) {
        this.type = type;
        this.value = value;
        this.user = user;
        this.post = post;
        this.timestamp = LocalDateTime.now(); // <--- OLUŞTURULDUĞU AN TARİHİ ATAR
    }

    // --- GETTER & SETTER ---
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public VoteType getType() { return type; }
    public void setType(VoteType type) { this.type = type; }

    public int getValue() { return value; }
    public void setValue(int value) { this.value = value; }
    
    public LocalDateTime getTimestamp() { return timestamp; } // <--- Getter
    public void setTimestamp(LocalDateTime timestamp) { this.timestamp = timestamp; } // <--- Setter

    public User getUser() { return user; }
    public void setUser(User user) { this.user = user; }

    public Post getPost() { return post; }
    public void setPost(Post post) { this.post = post; }
}