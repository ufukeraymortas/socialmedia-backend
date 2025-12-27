package com.senate.socialmedia.controller;

import com.senate.socialmedia.service.ElectionScheduler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/test")
public class TestController {

    @Autowired
    private ElectionScheduler electionScheduler;

    // Tarayıcıya yaz: http://localhost:8080/api/test/start-elections
    // Bu, tarihi beklemeden TÜM topluluklarda seçimi zorla başlatır.
    @GetMapping("/start-elections")
    public String forceStart() {
        electionScheduler.startAnnualElections();
        return "✅ TAMAM: Tüm topluluklarda seçimler ZORLA başlatıldı! Sayfayı yenileyip sandığı görebilirsin.";
    }

    // Tarayıcıya yaz: http://localhost:8080/api/test/finish-elections
    // Bu, seçimi zorla bitirir ve oyları sayıp başkanı atar.
    @GetMapping("/finish-elections")
    public String forceFinish() {
        electionScheduler.finishAnnualElections();
        return "🏁 TAMAM: Seçimler bitirildi, oylar sayıldı ve başkanlar atandı!";
    }
}