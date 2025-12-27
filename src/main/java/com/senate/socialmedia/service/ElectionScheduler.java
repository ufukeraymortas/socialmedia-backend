package com.senate.socialmedia.service;

import com.senate.socialmedia.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;

@Service
public class ElectionScheduler {

    @Autowired private CommunityRepository communityRepository;
    @Autowired private ElectionRepository electionRepository;

    // BAŞLATMA AYNI KALSIN (15 Eylül)
    @Scheduled(cron = "0 0 11 15 9 ?", zone = "Europe/Istanbul")
    @Transactional
    public void startAnnualElections() {
        // ... (Eski kodun aynısı) ...
        // Sadece test için buraya kopyalamıyorum, önceki cevaptakiyle aynı.
        // Ama istersen test için "fixedRate" kullanabilirsin.
        
        List<Community> communities = communityRepository.findAll();
        for (Community comm : communities) {
            if (electionRepository.findByCommunityIdAndIsActiveTrue(comm.getId()).isPresent()) continue;
            Election election = new Election();
            election.setCommunity(comm);
            election.setActive(true);
            election.setStartDate(LocalDateTime.now());
            electionRepository.save(election);
            System.out.println("✅ Sandık açıldı: " + comm.getName());
        }
    }

    // 🔴 BİTİRME VE KAZANANI BELİRLEME (KURALLI)
    @Scheduled(cron = "0 0 11 26 9 ?", zone = "Europe/Istanbul")
    @Transactional
    public void finishAnnualElections() {
        System.out.println("🏁 SEÇİMLER SONUÇLANIYOR...");

        List<Election> activeElections = electionRepository.findAll().stream()
                .filter(Election::isActive).toList();

        for (Election election : activeElections) {
            election.setActive(false);
            election.setEndDate(LocalDateTime.now());
            Community comm = election.getCommunity();
            User newPresident = null;

            List<Candidate> candidates = election.getCandidates();
            int candidateCount = candidates.size();

            // --- SENARYO 1: HİÇ ADAY YOK ---
            if (candidateCount == 0) {
                System.out.println("⚠️ (" + comm.getName() + ") Aday yok! En eski üye aranıyor...");
                
                // En eski üyeyi bul
                Optional<User> oldest = communityRepository.findOldestMember(comm.getId());
                if (oldest.isPresent()) {
                    newPresident = oldest.get();
                    System.out.println("👴 Otomatik Başkan (En Eski): " + newPresident.getUsername());
                } else {
                    // Üye bile yoksa Founder kalır
                    newPresident = comm.getFounder(); 
                    System.out.println("👻 Üye bile yok, Founder devam ediyor.");
                }
            } 
            // --- SENARYO 2: TEK ADAY ---
            else if (candidateCount == 1) {
                newPresident = candidates.get(0).getUser();
                System.out.println("🦄 Tek Aday Otomatik Kazandı: " + newPresident.getUsername());
            } 
            // --- SENARYO 3: ÇOKLU ADAY (OYLAMA) ---
            else {
                Optional<Candidate> winner = candidates.stream()
                        .max(Comparator.comparingInt(Candidate::getVoteCount));
                if (winner.isPresent()) {
                    newPresident = winner.get().getUser();
                    System.out.println("🏆 Seçim Kazananı: " + newPresident.getUsername());
                }
            }

            // ATAMA YAP
            if (newPresident != null) {
                comm.setPresident(newPresident);
                communityRepository.save(comm);
            }
            
            electionRepository.save(election);
        }
    }
}