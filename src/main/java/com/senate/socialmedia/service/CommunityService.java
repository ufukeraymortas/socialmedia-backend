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
    private FileStorageService fileStorageService;

    public List<Community> getAllCommunities() {
        return communityRepository.findAll();
    }

    public Community getCommunity(Long id) {
        return communityRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Topluluk bulunamadı."));
    }

    // TOPLULUK OLUŞTURMA (Kurucuyu otomatik üye yapıyoruz)
    @Transactional
    public Community createCommunity(String name, String description, boolean isPublic, Long creatorId, MultipartFile icon, MultipartFile banner) {
        User creator = userRepository.findById(creatorId)
                .orElseThrow(() -> new RuntimeException("Kullanıcı bulunamadı"));

        Community comm = new Community();
        comm.setName(name);
        comm.setDescription(description);
        comm.setPublic(isPublic);
        comm.setFounder(creator);
        comm.setCandidateQuota(10); // Varsayılan aday kotası

        // Kurucuyu otomatik olarak "Üyeler" listesine ekle (En eski üye olması için)
        comm.getMembers().add(creator);

        if (icon != null && !icon.isEmpty()) {
            comm.setIconUrl(fileStorageService.storeFile(icon));
        }
        if (banner != null && !banner.isEmpty()) {
            comm.setBannerUrl(fileStorageService.storeFile(banner));
        }

        return communityRepository.save(comm);
    }

    // TOPLULUĞA KATIL (JOIN) - Transactional önemli!
    @Transactional
    public void joinCommunity(Long communityId, Long userId) {
        Community comm = communityRepository.findById(communityId)
                .orElseThrow(() -> new RuntimeException("Topluluk yok"));
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("Kullanıcı yok"));

        // Set olduğu için zaten ekliyse tekrar eklemez, hata vermez.
        comm.getMembers().add(user);
        
        communityRepository.save(comm);
    }

    // SAHİPLENME (CLAIM)
    @Transactional
    public Community claimCommunity(Long communityId, Long userId) {
        Community comm = communityRepository.findById(communityId).orElseThrow();
        User user = userRepository.findById(userId).orElseThrow();

        if (comm.getFounder() == null) {
            comm.setFounder(user);
            // Sahip olan kişiyi üye de yapalım
            comm.getMembers().add(user);
            return communityRepository.save(comm);
        } else {
            throw new RuntimeException("Bu topluluğun zaten sahibi var.");
        }
    }
}