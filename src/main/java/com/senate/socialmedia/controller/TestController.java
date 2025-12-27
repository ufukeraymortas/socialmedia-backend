package com.senate.socialmedia.controller;

import com.senate.socialmedia.CommunityRankRepository;
import com.senate.socialmedia.CommunityRepository;
import com.senate.socialmedia.UserRepository;
import com.senate.socialmedia.VoteRepository;
import com.senate.socialmedia.ElectionRepository;
import com.senate.socialmedia.PostRepository;
import com.senate.socialmedia.CandidateRepository;
import com.senate.socialmedia.service.ElectionScheduler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.transaction.annotation.Transactional;

@RestController
@RequestMapping("/api/test")
public class TestController {

    @Autowired private ElectionScheduler electionScheduler;
    
    // Temizlik için tüm repolara ihtiyacımız var
    @Autowired private VoteRepository voteRepository;
    @Autowired private CandidateRepository candidateRepository;
    @Autowired private ElectionRepository electionRepository;
    @Autowired private PostRepository postRepository;
    @Autowired private CommunityRankRepository rankRepository;
    @Autowired private CommunityRepository communityRepository;
    @Autowired private UserRepository userRepository;

    // 1. ZORLA SEÇİM BAŞLAT
    @GetMapping("/start-elections")
    public String forceStart() {
        electionScheduler.startAnnualElections();
        return "✅ TAMAM: Tüm topluluklarda seçimler ZORLA başlatıldı!";
    }

    // 2. ZORLA SEÇİM BİTİR
    @GetMapping("/finish-elections")
    public String forceFinish() {
        electionScheduler.finishAnnualElections();
        return "🏁 TAMAM: Seçimler bitirildi ve başkanlar atandı!";
    }

    // 3. ☢️ ACİL DURUM BUTONU: SİSTEMİ SIFIRLA (HER ŞEYİ SİL) ☢️
    @GetMapping("/factory-reset")
    @Transactional
    public String factoryReset() {
        // Silme sırası çok önemli (Bağımlılıklar yüzünden)
        
        // 1. Önce oylar ve adaylar (En alt katman)
        voteRepository.deleteAll();
        candidateRepository.deleteAll();
        
        // 2. Seçimler ve Rütbeler
        electionRepository.deleteAll();
        rankRepository.deleteAll();
        
        // 3. Postlar
        postRepository.deleteAll();
        
        // 4. Topluluklar (Önce üye ilişkilerini koparmamız gerekebilir ama JPA halleder)
        communityRepository.deleteAll();
        
        // 5. En son Kullanıcılar
        userRepository.deleteAll();

        return "♻️ SİSTEM SIFIRLANDI! Tüm veriler silindi. Şimdi sayfayı yenileyip 'Kayıt Ol' diyerek sıfırdan başlayabilirsin.";
    }
}