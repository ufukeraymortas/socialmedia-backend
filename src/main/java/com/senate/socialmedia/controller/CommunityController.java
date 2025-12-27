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
    private UserRepository userRepository;

    @Autowired
    private FileStorageService fileStorageService;

    @Autowired
    private CommunityRankRepository rankRepository;

    @GetMapping
    public List<Community> getAllCommunities() {
        return communityRepository.findAll();
    }

    @PostMapping
    public Community createCommunity(
            @RequestParam String name,
            @RequestParam String description,
            @RequestParam boolean isPublic,
            @RequestParam Long creatorId,
            @RequestParam(required=false) MultipartFile icon,
            @RequestParam(required=false) MultipartFile banner) {
        
        User creator = userRepository.findById(creatorId)
                .orElseThrow(() -> new RuntimeException("Kullanıcı bulunamadı"));

        Community comm = new Community();
        comm.setName(name);
        comm.setDescription(description);
        comm.setPublic(isPublic);
        comm.setFounder(creator);

        if(icon != null && !icon.isEmpty()) comm.setIconUrl(fileStorageService.storeFile(icon));
        if(banner != null && !banner.isEmpty()) comm.setBannerUrl(fileStorageService.storeFile(banner));

        return communityRepository.save(comm);
    }

    @GetMapping("/{id}")
    public Community getCommunity(@PathVariable Long id) {
        return communityRepository.findById(id).orElseThrow();
    }

    @DeleteMapping("/{id}")
    public void deleteCommunity(@PathVariable Long id, @RequestParam Long requesterId) {
        Community comm = communityRepository.findById(id).orElseThrow();

        // GÜVENLİK GÜNCELLEMESİ: Kurucu NULL ise (Eski topluluksa) hata verme, silmeye izin ver.
        if (comm.getFounder() != null) {
            if (!comm.getFounder().getId().equals(requesterId)) {
                throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Yetkiniz yok! Sadece kurucu silebilir.");
            }
        }
        // Eğer founder null ise, kod buraya devam eder ve siler (Bu sayede eski toplulukları temizleyebilirsin)

        postRepository.deleteByCommunityId(id);
        communityRepository.deleteById(id);
    }

    @PostMapping("/{id}/ranks")
    public CommunityRank addRank(
            @PathVariable Long id, 
            @RequestParam String name, 
            @RequestParam int threshold,
            @RequestParam Long requesterId) {
        
        Community comm = communityRepository.findById(id).orElseThrow();

        // GÜVENLİK: Kurucu yoksa işlem yapma
        if (comm.getFounder() == null) {
             throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Bu topluluğun sahibi yok. Önce sahiplenmelisiniz.");
        }

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

    // --- YENİ: ESKİ TOPLULUKLARI KURTARMA (TAMİR) ---
    // Bu adrese istek atarak sahipsiz topluluğu üzerine alabilirsin.
    // Kullanım: POST /api/communities/{id}/claim?userId={seninIdn}
    @PostMapping("/{id}/claim")
    public Community claimCommunity(@PathVariable Long id, @RequestParam Long userId) {
        Community comm = communityRepository.findById(id).orElseThrow();
        User user = userRepository.findById(userId).orElseThrow();

        // Sadece sahibi yoksa verelim
        if(comm.getFounder() == null) {
            comm.setFounder(user);
            return communityRepository.save(comm);
        } else {
            throw new RuntimeException("Bu topluluğun zaten bir sahibi var: " + comm.getFounder().getUsername());
        }
    }
}