package com.senate.socialmedia;

import jakarta.persistence.*; 

@Entity
@Table(name = "post_likes")
public class Like {
    
    @Id 
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    // YENİ ALAN: Bu bir UP mı yoksa DOWN mı?
    @Enumerated(EnumType.STRING) // Veritabanına "UP" veya "DOWN" yazısı olarak kaydeder
    private VoteType voteType;

    @ManyToOne
    @JoinColumn(name = "user_id") 
    private User user;
    
    @ManyToOne
    @JoinColumn(name = "post_id") 
    private Post post;
    
    public Like() {
    }

    // Getter ve Setter'lar
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public VoteType getVoteType() {
        return voteType;
    }

    public void setVoteType(VoteType voteType) {
        this.voteType = voteType;
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