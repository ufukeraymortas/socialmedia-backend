package com.senate.socialmedia.controller;

import com.senate.socialmedia.Community;
import com.senate.socialmedia.CommunityRepository;
import com.senate.socialmedia.PostRepository; // Postları silmek için lazım
import com.senate.socialmedia.service.FileStorageService; // Resim yüklemek için lazım
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
    private PostRepository postRepository; // Silme işlemi için

    @Autowired
    private FileStorageService fileStorageService; // Resim işlemleri için

    // 1. Tüm Toplulukları Listele
    @GetMapping
    public List<Community> getAllCommunities() {
        return communityRepository.findAll();
    }

    // 2. Yeni Topluluk Oluştur (Resimli ve Gizlilik Ayarlı)
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

        // İkon varsa kaydet
        if(icon != null && !icon.isEmpty()) {
            String iconName = fileStorageService.storeFile(icon);
            comm.setIconUrl(iconName);
        }

        // Banner varsa kaydet
        if(banner != null && !banner.isEmpty()) {
            String bannerName = fileStorageService.storeFile(banner);
            comm.setBannerUrl(bannerName);
        }

        return communityRepository.save(comm);
    }

    // 3. Tek Bir Topluluğun Detayını Getir
    @GetMapping("/{id}")
    public Community getCommunity(@PathVariable Long id) {
        return communityRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Topluluk bulunamadı id: " + id));
    }

    // 4. Topluluğu Feshet (Sil) - Önce Postları Temizler
    @DeleteMapping("/{id}")
    public void deleteCommunity(@PathVariable Long id) {
        // Önce bu topluluğa ait postları sil (Veritabanı hatasını önlemek için)
        postRepository.deleteByCommunityId(id);
        
        // Şimdi topluluğu sil
        communityRepository.deleteById(id);
    }
}