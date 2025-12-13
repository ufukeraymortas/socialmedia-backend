package com.senate.socialmedia;

import jakarta.persistence.*;
import com.fasterxml.jackson.annotation.JsonIgnore; // Sonsuz döngü önleyici
import java.util.Set;
import java.util.HashSet;

@Entity
@Table(name = "users")
public class User {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String username;
    
    @Column(nullable = false)
    private String password;
    
    // Profil Bilgileri
    private String title; // Ünvan
    private String bio;   // Biyografi
    private String profilePictureUrl; // Profil Resmi
    private String headerUrl; // Kapak Resmi (Header)

    @ManyToMany
    @JoinTable(
        name = "user_followers",
        joinColumns = @JoinColumn(name = "follower_id"),
        inverseJoinColumns = @JoinColumn(name = "followed_id")
    )
    private Set<User> following = new HashSet<>();

    // Beni takip edenler
    @ManyToMany(mappedBy = "following")
    @JsonIgnore // Beni takip edenleri çekerken, onların takip ettiklerini çekmeye çalışma (Döngüye girer)
    private Set<User> followers = new HashSet<>();

    // --- CONSTRUCTOR & GETTER-SETTER ---

    public User() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getBio() {
        return bio;
    }

    public void setBio(String bio) {
        this.bio = bio;
    }

    public String getProfilePictureUrl() {
        return profilePictureUrl;
    }

    public void setProfilePictureUrl(String profilePictureUrl) {
        this.profilePictureUrl = profilePictureUrl;
    }

    public String getHeaderUrl() {
        return headerUrl;
    }

    public void setHeaderUrl(String headerUrl) {
        this.headerUrl = headerUrl;
    }

    // Takip Getter/Setter
    public Set<User> getFollowing() {
        return following;
    }

    public void setFollowing(Set<User> following) {
        this.following = following;
    }

    public Set<User> getFollowers() {
        return followers;
    }

    public void setFollowers(Set<User> followers) {
        this.followers = followers;
    }
}