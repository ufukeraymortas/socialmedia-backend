package com.senate.socialmedia;

import jakarta.persistence.*;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "users")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private String username;

    private String password;
    private String title;  // Örn: "Senatör", "Vatandaş"
    private String bio;    // Profil biyografisi
    private String profilePictureUrl;
    private String headerUrl;
    
    // 🔥 EKSİK OLAN KISIM BURASIYDI 🔥
    private int karma = 0; // Varsayılan puan 0

    // İlişkiler (Takipçiler ve Takip Edilenler)
    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
        name = "user_followers",
        joinColumns = @JoinColumn(name = "user_id"),
        inverseJoinColumns = @JoinColumn(name = "follower_id")
    )
    private Set<User> followers = new HashSet<>();

    @ManyToMany(mappedBy = "followers", fetch = FetchType.EAGER)
    private Set<User> following = new HashSet<>();

    // --- GETTER VE SETTER METOTLARI ---

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }

    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getBio() { return bio; }
    public void setBio(String bio) { this.bio = bio; }

    public String getProfilePictureUrl() { return profilePictureUrl; }
    public void setProfilePictureUrl(String profilePictureUrl) { this.profilePictureUrl = profilePictureUrl; }

    public String getHeaderUrl() { return headerUrl; }
    public void setHeaderUrl(String headerUrl) { this.headerUrl = headerUrl; }

    // 🔥 setKarma BURADA! 🔥
    public int getKarma() { return karma; }
    public void setKarma(int karma) { this.karma = karma; }

    public Set<User> getFollowers() { return followers; }
    public void setFollowers(Set<User> followers) { this.followers = followers; }

    public Set<User> getFollowing() { return following; }
    public void setFollowing(Set<User> following) { this.following = following; }
}