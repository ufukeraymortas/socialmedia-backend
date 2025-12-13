package com.senate.socialmedia.service;

import com.senate.socialmedia.Community;
import com.senate.socialmedia.CommunityRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class CommunityService {

    @Autowired
    private CommunityRepository communityRepository;

    // Yeni Topluluk Oluştur
    public Community createCommunity(String name, String description) {
        // Aynı isimde var mı kontrol et
        if (communityRepository.findByName(name).isPresent()) {
            throw new RuntimeException("Bu isimde bir topluluk zaten var!");
        }
        Community community = new Community();
        community.setName(name);
        community.setDescription(description);
        return communityRepository.save(community);
    }

    // Tüm Toplulukları Getir
    public List<Community> getAllCommunities() {
        return communityRepository.findAll();
    }
    
 // Controller'dan çağırıp güncel halini kaydetmek için
    public Community save(Community community) {
        return communityRepository.save(community);
    }
}