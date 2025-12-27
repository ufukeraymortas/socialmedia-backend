package com.senate.socialmedia;

import jakarta.persistence.*;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Set; // <--- EKLENDİ
import java.util.HashSet; // <--- EKLENDİ

@Entity
@Table(name = "elections")
public class Election {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private boolean isActive;
    private LocalDateTime startDate;
    private LocalDateTime endDate;

    @ManyToOne
    @JoinColumn(name = "community_id", nullable = false)
    @JsonIgnoreProperties("president")
    private Community community;

    @OneToMany(mappedBy = "election", cascade = CascadeType.ALL)
    @JsonIgnoreProperties("election")
    private List<Candidate> candidates;

    // --- YENİ: KİMLER OY KULLANDI? (HİLE KORUMASI) ---
    @ElementCollection
    private Set<Long> voterIds = new HashSet<>(); 
    // -----------------------------------------------

    public Election() {
        this.startDate = LocalDateTime.now();
        this.isActive = true;
    }

    // GETTER & SETTER
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public boolean isActive() { return isActive; }
    public void setActive(boolean active) { isActive = active; }

    public LocalDateTime getStartDate() { return startDate; }
    public void setStartDate(LocalDateTime startDate) { this.startDate = startDate; }

    public LocalDateTime getEndDate() { return endDate; }
    public void setEndDate(LocalDateTime endDate) { this.endDate = endDate; }

    public Community getCommunity() { return community; }
    public void setCommunity(Community community) { this.community = community; }

    public List<Candidate> getCandidates() { return candidates; }
    public void setCandidates(List<Candidate> candidates) { this.candidates = candidates; }

    public Set<Long> getVoterIds() { return voterIds; }
    public void setVoterIds(Set<Long> voterIds) { this.voterIds = voterIds; }
}