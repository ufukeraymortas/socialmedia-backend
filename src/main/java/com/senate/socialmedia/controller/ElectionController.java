package com.senate.socialmedia.controller;

import com.senate.socialmedia.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@RestController
@RequestMapping("/api/elections")
@CrossOrigin(origins = "*")
public class ElectionController {

    @Autowired private ElectionRepository electionRepository;
    @Autowired private CandidateRepository candidateRepository;
    @Autowired private UserRepository userRepository;
    @Autowired private CommunityRepository communityRepository;
    @Autowired private VoteRepository voteRepository; // Karma kontrolü için

    @GetMapping("/active/{communityId}")
    public Election getActiveElection(@PathVariable Long communityId) {
        return electionRepository.findByCommunityIdAndIsActiveTrue(communityId).orElse(null);
    }

    // 1. TOPLULUĞA KATIL (Member Ol) - "En Eski Üye" mantığı için şart
    @PostMapping("/join/{communityId}")
    public void joinCommunity(@PathVariable Long communityId, @RequestParam Long userId) {
        Community comm = communityRepository.findById(communityId).orElseThrow();
        User user = userRepository.findById(userId).orElseThrow();
        comm.getMembers().add(user);
        communityRepository.save(comm);
    }

    // 2. KOTA AYARLA (Sadece Başkan/Kurucu)
    @PostMapping("/settings/{communityId}")
    public void setCandidateQuota(
            @PathVariable Long communityId, 
            @RequestParam int quota, 
            @RequestParam Long requesterId) {
        
        Community comm = communityRepository.findById(communityId).orElseThrow();
        
        boolean isFounder = comm.getFounder() != null && comm.getFounder().getId().equals(requesterId);
        boolean isPresident = comm.getPresident() != null && comm.getPresident().getId().equals(requesterId);

        if (!isFounder && !isPresident) throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Yetkisiz işlem.");

        comm.setCandidateQuota(quota);
        communityRepository.save(comm);
    }

    // 3. ADAY OL (Karma Kontrollü)
    @PostMapping("/{electionId}/candidates")
    public Candidate becomeCandidate(@PathVariable Long electionId, @RequestParam Long userId) {
        Election election = electionRepository.findById(electionId).orElseThrow();
        User user = userRepository.findById(userId).orElseThrow();
        Community comm = election.getCommunity();

        if (!election.isActive()) throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Seçim aktif değil.");

        // Zaten aday mı?
        if (election.getCandidates().stream().anyMatch(c -> c.getUser().getId().equals(userId))) {
            throw new RuntimeException("Zaten adaysınız.");
        }

        // --- KARMA KONTROLÜ (İLK X KİŞİDE MİSİN?) ---
        // 1. O topluluktaki en yüksek karmalı kullanıcıları çek
        List<User> topUsers = voteRepository.findTopKarmaUsers(comm.getId());
        
        // 2. Limit kadarını al (Örn: İlk 10)
        int limit = comm.getCandidateQuota();
        if (topUsers.size() > limit) {
            topUsers = topUsers.subList(0, limit);
        }

        // 3. Kullanıcı bu listede mi?
        boolean isEligible = topUsers.stream().anyMatch(u -> u.getId().equals(userId));

        if (!isEligible) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, 
                "Aday olamazsınız! Karması en yüksek ilk " + limit + " kişi arasında olmanız lazım.");
        }
        // ---------------------------------------------

        Candidate candidate = new Candidate();
        candidate.setElection(election);
        candidate.setUser(user);
        return candidateRepository.save(candidate);
    }

    @PostMapping("/vote")
    public void vote(@RequestParam Long candidateId, @RequestParam Long voterId) {
        Candidate candidate = candidateRepository.findById(candidateId).orElseThrow();
        Election election = candidate.getElection();

        if (!election.isActive()) throw new RuntimeException("Seçim kapalı.");
        if (election.getVoterIds().contains(voterId)) throw new RuntimeException("Zaten oy kullandınız!");

        candidate.setVoteCount(candidate.getVoteCount() + 1);
        election.getVoterIds().add(voterId);

        candidateRepository.save(candidate);
        electionRepository.save(election);
    }
}