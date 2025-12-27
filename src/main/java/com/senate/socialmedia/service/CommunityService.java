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
    private PostRepository postRepository; // Silme işlemi için lazım

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

    // --- DÜZELTİLDİ: ESKİ TOPLULUKLARI SİLME ---
    @Transactional
    public void deleteCommunity(Long communityId, Long requesterId) {
        Community comm = communityRepository.findById(communityId).orElseThrow();

        // 1. Eğer kurucu NULL ise (Eski Kayıt): Direkt silmeye izin ver (Temizlik için)
        // 2. Eğer kurucu varsa: İsteyen kişi kurucu mu diye bak.
        if (comm.getFounder() != null) {
            if (!comm.getFounder().getId().equals(requesterId)) {
                throw new RuntimeException("Yetkiniz yok! Sadece kurucu silebilir.");
            }
        }
        
        postRepository.deleteByCommunityId(communityId);
        communityRepository.deleteById(communityId);
    }

    // --- YENİ: RESİM GÜNCELLEME (Kurucu veya Başkan) ---
    @Transactional
    public Community updateCommunityVisuals(Long communityId, Long requesterId, MultipartFile icon, MultipartFile banner) {
        Community comm = communityRepository.findById(communityId).orElseThrow();
        
        boolean isFounder = comm.getFounder() != null && comm.getFounder().getId().equals(requesterId);
        boolean isPresident = comm.getPresident() != null && comm.getPresident().getId().equals(requesterId);

        if (!isFounder && !isPresident) {
            throw new RuntimeException("Yetkiniz yok. Sadece Başkan veya Kurucu değiştirebilir.");
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