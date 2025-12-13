package com.senate.socialmedia.controller;

import com.senate.socialmedia.Community;
import com.senate.socialmedia.service.CommunityService;
import com.senate.socialmedia.service.FileStorageService; // Resim servisini ekledik
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile; // Dosya yükleme için

import java.util.List;

@RestController
@RequestMapping("/api/communities")
@CrossOrigin(origins = "*")
public class CommunityController {

    @Autowired
    private CommunityService communityService;

    @Autowired
    private FileStorageService fileStorageService; // Dosya kaydetmek için

    // Topluluk Oluştur (Artık Resim Destekli)
    @PostMapping
    public Community createCommunity(
            @RequestParam("name") String name,
            @RequestParam("description") String description,
            @RequestParam("isPublic") boolean isPublic,
            @RequestParam(value = "icon", required = false) MultipartFile icon,
            @RequestParam(value = "banner", required = false) MultipartFile banner
    ) {
        Community community = communityService.createCommunity(name, description);
        
        // Gizlilik ayarını güncelle
        community.setPublic(isPublic);

        // İkon varsa kaydet
        if (icon != null && !icon.isEmpty()) {
            String iconName = fileStorageService.storeFile(icon);
            community.setIconUrl(iconName);
        }

        // Banner varsa kaydet
        if (banner != null && !banner.isEmpty()) {
            String bannerName = fileStorageService.storeFile(banner);
            community.setBannerUrl(bannerName);
        }
        
        // Güncellemeleri kaydetmek için tekrar save çağırmalıyız veya service içinde halletmeliyiz.
        // Hızlı çözüm için repository'e buradan erişmek yerine service'e save methodu ekleyebiliriz
        // ama CommunityService'deki 'createCommunity' zaten 'save' döndürüyor.
        // O yüzden burada icon set edip tekrar save etmemiz lazım.
        // Basitlik adına, CommunityService'e bir update metodu eklemeden,
        // direkt repository'i buraya autowire etmek yerine Service'e bir 'save' metodu ekleyelim.
        
        return communityService.save(community); 
    }

    @GetMapping
    public List<Community> getAllCommunities() {
        return communityService.getAllCommunities();
    }
}