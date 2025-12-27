package com.senate.socialmedia.controller;

import com.senate.socialmedia.*;
import com.senate.socialmedia.service.CommunityService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@RestController
@RequestMapping("/api/communities")
@CrossOrigin(origins = "*")
public class CommunityController {

    @Autowired
    private CommunityService communityService;

    @Autowired
    private CommunityRepository communityRepository; // Okuma işlemleri için
    @Autowired
    private CommunityRankRepository rankRepository;

    @GetMapping
    public List<Community> getAllCommunities() {
        return communityService.getAllCommunities();
    }

    @PostMapping
    public Community createCommunity(
            @RequestParam String name,
            @RequestParam String description,
            @RequestParam boolean isPublic,
            @RequestParam Long creatorId,
            @RequestParam(required=false) MultipartFile icon,
            @RequestParam(required=false) MultipartFile banner) {
        return communityService.createCommunity(name, description, isPublic, creatorId, icon, banner);
    }

    @GetMapping("/{id}")
    public Community getCommunity(@PathVariable Long id) {
        return communityService.getCommunity(id);
    }

    @PostMapping("/{id}/join")
    public void joinCommunity(@PathVariable Long id, @RequestParam Long userId) {
        communityService.joinCommunity(id, userId);
    }

    @PostMapping("/{id}/claim")
    public Community claimCommunity(@PathVariable Long id, @RequestParam Long userId) {
        return communityService.claimCommunity(id, userId);
    }

    @DeleteMapping("/{id}")
    public void deleteCommunity(@PathVariable Long id, @RequestParam Long requesterId) {
        try {
            communityService.deleteCommunity(id, requesterId);
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, e.getMessage());
        }
    }

    // --- YENİ: RESİM GÜNCELLEME ---
    @PutMapping("/{id}/images")
    public Community updateImages(
            @PathVariable Long id,
            @RequestParam Long requesterId,
            @RequestParam(required = false) MultipartFile icon,
            @RequestParam(required = false) MultipartFile banner) {
        try {
            return communityService.updateCommunityVisuals(id, requesterId, icon, banner);
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, e.getMessage());
        }
    }

    @PostMapping("/{id}/ranks")
    public CommunityRank addRank(
            @PathVariable Long id, 
            @RequestParam String name, 
            @RequestParam int threshold,
            @RequestParam Long requesterId) {
        
        Community comm = communityRepository.findById(id).orElseThrow();
        if (comm.getFounder() == null) throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Sahibi yok.");
        if (!comm.getFounder().getId().equals(requesterId)) throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Yetkisiz.");
        
        CommunityRank rank = new CommunityRank();
        rank.setName(name);
        rank.setThreshold(threshold);
        rank.setCommunity(comm);
        return rankRepository.save(rank);
    }

    @GetMapping("/{id}/ranks")
    public List<CommunityRank> getRanks(@PathVariable Long id) {
        return rankRepository.findByCommunityIdOrderByThresholdDesc(id);
    }
}