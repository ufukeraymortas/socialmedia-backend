package com.senate.socialmedia;

import jakarta.persistence.*;
import com.fasterxml.jackson.annotation.JsonIgnore;

@Entity
@Table(name = "community_ranks")
public class CommunityRank {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;      // Rütbe Adı (Örn: Usta)
    private int threshold;    // Puan Barajı (Örn: 50)

    @ManyToOne
    @JoinColumn(name = "community_id", nullable = false)
    @JsonIgnore // Sonsuz döngü olmasın diye
    private Community community;

    // --- GETTER & SETTER ---
    public CommunityRank() {}

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public int getThreshold() { return threshold; }
    public void setThreshold(int threshold) { this.threshold = threshold; }

    public Community getCommunity() { return community; }
    public void setCommunity(Community community) { this.community = community; }
}