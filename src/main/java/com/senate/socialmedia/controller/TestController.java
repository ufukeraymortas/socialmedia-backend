package com.senate.socialmedia.controller;

// --- BURAYA DİKKAT: Importları tek tek açıkça yazdım ---
import com.senate.socialmedia.Community;
import com.senate.socialmedia.service.ElectionScheduler;

// Repository'lerinizin hepsi "repository" paketinde mi?
// Eğer hepsi ana klasördeyse bu "repository." kısımlarını silmelisin.
import com.senate.socialmedia.VoteRepository;
import com.senate.socialmedia.CandidateRepository;
import com.senate.socialmedia.ElectionRepository;
import com.senate.socialmedia.PostRepository;
import com.senate.socialmedia.CommunityRankRepository;
import com.senate.socialmedia.CommunityRepository;
import com.senate.socialmedia.UserRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.HashSet;

@RestController
@RequestMapping("/api/test")
public class TestController {

    @Autowired private ElectionScheduler electionScheduler;
    @Autowired private VoteRepository voteRepository;
    @Autowired private CandidateRepository candidateRepository;
    @Autowired private ElectionRepository electionRepository;
    @Autowired private PostRepository postRepository;
    @Autowired private CommunityRankRepository rankRepository;
    @Autowired private CommunityRepository communityRepository;
    @Autowired private UserRepository userRepository;

    @GetMapping("/start-elections")
    public String forceStart() {
        electionScheduler.startAnnualElections();
        return "✅ Seçimler Başlatıldı!";
    }

    @GetMapping("/finish-elections")
    public String forceFinish() {
        electionScheduler.finishAnnualElections();
        return "🏁 Seçimler Bitirildi!";
    }

    // 🔥 KİLİT AÇICI SIFIRLAMA KODU 🔥
    @GetMapping("/factory-reset")
    @Transactional
    public String factoryReset() {
        // 1. Önce Toplulukların içindeki bağları kopar (Zinciri Kır)
        List<Community> communities = communityRepository.findAll();
        for (Community c : communities) {
            c.setFounder(null);    // Kurucuyu unut
            c.setPresident(null);  // Başkanı unut
            c.setMembers(new HashSet<>()); // Üyeleri boşalt
            communityRepository.save(c);
        }

        // 2. Şimdi Alt Tabloları Sil
        voteRepository.deleteAll();
        candidateRepository.deleteAll();
        electionRepository.deleteAll();
        rankRepository.deleteAll();
        postRepository.deleteAll();
        
        // 3. Artık Toplulukları silebiliriz (Bağ kalmadı)
        communityRepository.deleteAll();
        
        // 4. En son Kullanıcıları sil
        userRepository.deleteAll();

        return "♻️ SİSTEM BAŞARIYLA SIFIRLANDI! (500 Hatası Çözüldü). Şimdi siteye dönüp 'Kayıt Ol' diyebilirsin.";
    }
}