package com.senate.socialmedia.controller;

import com.senate.socialmedia.*;
import com.senate.socialmedia.service.FileStorageService;
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
    private CommunityRepository communityRepository;

    @Autowired
    private PostRepository postRepository;

    @Autowired
    private UserRepository userRepository; // Kurucuyu bulmak için lazım

    @Autowired
    private FileStorageService fileStorageService;

    @Autowired
    private CommunityRankRepository rankRepository;

    @GetMapping
    public List<Community> getAllCommunities() {
        return communityRepository.findAll();
    }

    // YENİ: Topluluk Oluştur (Kurucuyu Kaydet)
    @PostMapping
    public Community createCommunity(
            @RequestParam String name,
            @RequestParam String description,
            @RequestParam boolean isPublic,
            @RequestParam Long creatorId, // <--- ARTIK KURUCU ID'Sİ İSTİYORUZ
            @RequestParam(required=false) MultipartFile icon,
            @RequestParam(required=false) MultipartFile banner) {
        
        User creator = userRepository.findById(creatorId)
                .orElseThrow(() -> new RuntimeException("Kullanıcı bulunamadı"));

        Community comm = new Community();
        comm.setName(name);
        comm.setDescription(description);
        comm.setPublic(isPublic);
        comm.setFounder(creator); // <--- KURUCUYU ATADIK

        if(icon != null && !icon.isEmpty()) comm.setIconUrl(fileStorageService.storeFile(icon));
        if(banner != null && !banner.isEmpty()) comm.setBannerUrl(fileStorageService.storeFile(banner));

        return communityRepository.save(comm);
    }

    @GetMapping("/{id}")
    public Community getCommunity(@PathVariable Long id) {
        return communityRepository.findById(id).orElseThrow();
    }

    // GÜVENLİ SİLME (Sadece Kurucu Silebilir)
    @DeleteMapping("/{id}")
    public void deleteCommunity(@PathVariable Long id, @RequestParam Long requesterId) {
        Community comm = communityRepository.findById(id).orElseThrow();

        // KONTROL: İsteyen kişi kurucu mu?
        if (!comm.getFounder().getId().equals(requesterId)) {
            // İleride buraya "|| requesterId == comm.getCurrentPresident().getId()" eklenecek
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Buna yetkiniz yok! Sadece kurucu feshedebilir.");
        }

        postRepository.deleteByCommunityId(id);
        communityRepository.deleteById(id);
    }

    // RÜTBE EKLEME (Sadece Kurucu Ekleyebilir)
    @PostMapping("/{id}/ranks")
    public CommunityRank addRank(
            @PathVariable Long id, 
            @RequestParam String name, 
            @RequestParam int threshold,
            @RequestParam Long requesterId) { // <--- İsteyen kişi kim?
        
        Community comm = communityRepository.findById(id).orElseThrow();

        // KONTROL: İsteyen kişi kurucu mu?
        if (!comm.getFounder().getId().equals(requesterId)) {
             throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Yetkisiz işlem.");
        }
        
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