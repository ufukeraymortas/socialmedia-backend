package com.senate.socialmedia.controller;

import com.senate.socialmedia.*;
import com.senate.socialmedia.service.CommunityService; // Service'i import ettik
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
    private CommunityService communityService; // Repository yerine Service kullanıyoruz

    @Autowired
    private CommunityRepository communityRepository; // Sadece basit okumalar ve silme için kalabilir
    @Autowired
    private PostRepository postRepository;
    @Autowired
    private CommunityRankRepository rankRepository;

    // 1. LİSTELE
    @GetMapping
    public List<Community> getAllCommunities() {
        return communityService.getAllCommunities();
    }

    // 2. OLUŞTUR (Service kullanır)
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

    // 3. DETAY GETİR
    @GetMapping("/{id}")
    public Community getCommunity(@PathVariable Long id) {
        return communityService.getCommunity(id);
    }

    // 4. KATIL (JOIN) - YENİ
    @PostMapping("/{id}/join")
    public void joinCommunity(@PathVariable Long id, @RequestParam Long userId) {
        communityService.joinCommunity(id, userId);
    }

    // 5. SAHİPLEN (CLAIM)
    @PostMapping("/{id}/claim")
    public Community claimCommunity(@PathVariable Long id, @RequestParam Long userId) {
        return communityService.claimCommunity(id, userId);
    }

    // 6. SİLME (Hala burada kalabilir veya Service'e taşınabilir, şimdilik burada kalsın)
    @DeleteMapping("/{id}")
    public void deleteCommunity(@PathVariable Long id, @RequestParam Long requesterId) {
        Community comm = communityRepository.findById(id).orElseThrow();

        if (comm.getFounder() != null) {
            if (!comm.getFounder().getId().equals(requesterId)) {
                throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Yetkiniz yok!");
            }
        }
        postRepository.deleteByCommunityId(id);
        communityRepository.deleteById(id);
    }

    // 7. RÜTBE EKLEME
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