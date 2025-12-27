package com.senate.socialmedia;

import jakarta.persistence.*;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "communities")
public class Community {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;
    private String description;
    private String bannerUrl;
    private String iconUrl;
    private boolean isPublic;

    // --- DÜZELTME: Integer yaptık (Null hatasını önler) ---
    @Column(columnDefinition = "integer default 10") 
    private Integer candidateQuota = 10; 

    @ManyToOne
    @JoinColumn(name = "founder_id")
    @JsonIgnoreProperties({"posts", "votes", "following", "followers", "password"}) 
    private User founder;

    @ManyToOne
    @JoinColumn(name = "president_id")
    @JsonIgnoreProperties({"posts", "votes", "following", "followers", "password"}) 
    private User president;

    @ManyToMany
    @JoinTable(
        name = "community_members",
        joinColumns = @JoinColumn(name = "community_id"),
        inverseJoinColumns = @JoinColumn(name = "user_id")
    )
    @JsonIgnoreProperties({"posts", "votes", "following", "followers", "password", "communities"})
    private Set<User> members = new HashSet<>();
    
    public Community() {}

    // GETTER & SETTER
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    
    public String getBannerUrl() { return bannerUrl; }
    public void setBannerUrl(String bannerUrl) { this.bannerUrl = bannerUrl; }
    
    public String getIconUrl() { return iconUrl; }
    public void setIconUrl(String iconUrl) { this.iconUrl = iconUrl; }
    
    public boolean isPublic() { return isPublic; }
    public void setPublic(boolean aPublic) { isPublic = aPublic; }
    
    public User getFounder() { return founder; }
    public void setFounder(User founder) { this.founder = founder; }
    
    public User getPresident() { return president; }
    public void setPresident(User president) { this.president = president; }
    
    public Integer getCandidateQuota() { return candidateQuota; }
    public void setCandidateQuota(Integer candidateQuota) { this.candidateQuota = candidateQuota; }
    
    public Set<User> getMembers() { return members; }
    public void setMembers(Set<User> members) { this.members = members; }
}