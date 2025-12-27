package com.senate.socialmedia.controller;

import com.senate.socialmedia.*;
import com.senate.socialmedia.service.FileStorageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

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
    private FileStorageService fileStorageService;

    @Autowired
    private CommunityRankRepository rankRepository; // Rütbeler için

    // 1. Tüm Toplulukları Listele
    @GetMapping
    public List<Community> getAllCommunities() {
        return communityRepository.findAll();
    }

    // 2. Yeni Topluluk Oluştur
    @PostMapping
    public Community createCommunity(
            @RequestParam String name,
            @RequestParam String description,
            @RequestParam boolean isPublic,
            @RequestParam(required=false) MultipartFile icon,
            @RequestParam(required=false) MultipartFile banner) {
        
        Community comm = new Community();
        comm.setName(name);
        comm.setDescription(description);
        comm.setPublic(isPublic);

        if(icon != null && !icon.isEmpty()) {
            String iconName = fileStorageService.storeFile(icon);
            comm.setIconUrl(iconName);
        }

        if(banner != null && !banner.isEmpty()) {
            String bannerName = fileStorageService.storeFile(banner);
            comm.setBannerUrl(bannerName);
        }

        return communityRepository.save(comm);
    }

    // 3. Tek Bir Topluluğu Getir
    @GetMapping("/{id}")
    public Community getCommunity(@PathVariable Long id) {
        return communityRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Topluluk bulunamadı id: " + id));
    }

    // 4. Topluluğu Feshet (Güvenli Silme)
    @DeleteMapping("/{id}")
    public void deleteCommunity(@PathVariable Long id) {
        postRepository.deleteByCommunityId(id); // Önce postları temizle
        communityRepository.deleteById(id); // Sonra topluluğu sil
    }

    // --- RÜTBE İŞLEMLERİ (YENİ) ---

    // 5. Rütbe Ekle
    @PostMapping("/{id}/ranks")
    public CommunityRank addRank(@PathVariable Long id, @RequestParam String name, @RequestParam int threshold) {
        Community comm = communityRepository.findById(id).orElseThrow();
        
        CommunityRank rank = new CommunityRank();
        rank.setName(name);
        rank.setThreshold(threshold);
        rank.setCommunity(comm);
        
        return rankRepository.save(rank);
    }

    // 6. Rütbeleri Listele
    @GetMapping("/{id}/ranks")
    public List<CommunityRank> getRanks(@PathVariable Long id) {
        return rankRepository.findByCommunityIdOrderByThresholdDesc(id);
    }
}