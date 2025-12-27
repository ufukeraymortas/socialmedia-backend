package com.senate.socialmedia;

import jakarta.persistence.*;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@Entity
@Table(name = "candidates")
public class Candidate {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private int voteCount = 0; // Aldığı oy sayısı

    // Kim aday?
    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    @JsonIgnoreProperties({"posts", "votes", "following", "followers", "password"})
    private User user;

    // Hangi seçimde aday?
    @ManyToOne
    @JoinColumn(name = "election_id", nullable = false)
    @JsonIgnoreProperties("candidates")
    private Election election;
    
    // (İleride buraya "Vadi / Sloganı" alanını ekleyebiliriz: private String manifesto;)

    public Candidate() {}

    // GETTER & SETTER
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public int getVoteCount() { return voteCount; }
    public void setVoteCount(int voteCount) { this.voteCount = voteCount; }

    public User getUser() { return user; }
    public void setUser(User user) { this.user = user; }

    public Election getElection() { return election; }
    public void setElection(Election election) { this.election = election; }
}