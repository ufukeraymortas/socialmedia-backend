package com.senate.socialmedia.service;

import com.senate.socialmedia.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Service
public class CommunityService {

    @Autowired
    private CommunityRepository communityRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PostRepository postRepository;

    @Autowired
    private FileStorageService fileStorageService;

    public List<Community> getAllCommunities() {
        return communityRepository.findAll();
    }

    public Community getCommunity(Long id) {
        return communityRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Topluluk bulunamadı."));
    }

    @Transactional
    public Community createCommunity(String name, String description, boolean isPublic, Long creatorId, MultipartFile icon, MultipartFile banner) {
        User creator = userRepository.findById(creatorId)
                .orElseThrow(() -> new RuntimeException("Kullanıcı bulunamadı"));

        Community comm = new Community();
        comm.setName(name);
        comm.setDescription(description);
        comm.setPublic(isPublic);
        comm.setFounder(creator);
        comm.setCandidateQuota(10); 
        comm.getMembers().add(creator);

        if (icon != null && !icon.isEmpty()) comm.setIconUrl(fileStorageService.storeFile(icon));
        if (banner != null && !banner.isEmpty()) comm.setBannerUrl(fileStorageService.storeFile(banner));

        return communityRepository.save(comm);
    }

    @Transactional
    public void joinCommunity(Long communityId, Long userId) {
        Community comm = communityRepository.findById(communityId).orElseThrow();
        User user = userRepository.findById(userId).orElseThrow();
        comm.getMembers().add(user);
        communityRepository.save(comm);
    }

    @Transactional
    public Community claimCommunity(Long communityId, Long userId) {
        Community comm = communityRepository.findById(communityId).orElseThrow();
        User user = userRepository.findById(userId).orElseThrow();
        if (comm.getFounder() == null) {
            comm.setFounder(user);
            comm.getMembers().add(user);
            return communityRepository.save(comm);
        } else {
            throw new RuntimeException("Zaten sahibi var.");
        }
    }

    // --- GÜNCELLENDİ: SÜPER ADMİN YETKİSİ ---
    @Transactional
    public void deleteCommunity(Long communityId, Long requesterId) {
        Community comm = communityRepository.findById(communityId).orElseThrow();
        User requester = userRepository.findById(requesterId).orElseThrow();

        // 1. ÖZEL KONTROL: Eğer kullanıcı "ufukeraymortas" ise HER ŞEYİ SİLEBİLİR!
        if (requester.getUsername().equals("ufukeraymortas")) {
            postRepository.deleteByCommunityId(communityId);
            communityRepository.deleteById(communityId);
            return; // İşlem tamam, fonksiyondan çık
        }

        // 2. Normal Kontrol (Kurucu mu?)
        if (comm.getFounder() != null) {
            if (!comm.getFounder().getId().equals(requesterId)) {
                throw new RuntimeException("Yetkiniz yok! Sadece kurucu (veya ufukeraymortas) silebilir.");
            }
        }
        
        postRepository.deleteByCommunityId(communityId);
        communityRepository.deleteById(communityId);
    }

    @Transactional
    public Community updateCommunityVisuals(Long communityId, Long requesterId, MultipartFile icon, MultipartFile banner) {
        Community comm = communityRepository.findById(communityId).orElseThrow();
        User requester = userRepository.findById(requesterId).orElseThrow();
        
        // Buraya da ekleyelim: ufukeraymortas ise düzenleyebilir
        boolean isSuperUser = requester.getUsername().equals("ufukeraymortas");
        boolean isFounder = comm.getFounder() != null && comm.getFounder().getId().equals(requesterId);
        boolean isPresident = comm.getPresident() != null && comm.getPresident().getId().equals(requesterId);

        if (!isFounder && !isPresident && !isSuperUser) {
            throw new RuntimeException("Yetkiniz yok.");
        }

        if (icon != null && !icon.isEmpty()) {
            comm.setIconUrl(fileStorageService.storeFile(icon));
        }
        if (banner != null && !banner.isEmpty()) {
            comm.setBannerUrl(fileStorageService.storeFile(banner));
        }

        return communityRepository.save(comm);
    }
}