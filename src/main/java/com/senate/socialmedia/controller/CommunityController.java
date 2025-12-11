package com.senate.socialmedia.controller;

import com.senate.socialmedia.Community;
import com.senate.socialmedia.service.CommunityService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/communities")
public class CommunityController {

    @Autowired
    private CommunityService communityService;

    // Topluluk Oluştur: POST /api/communities
    @PostMapping
    public Community createCommunity(@RequestBody Map<String, String> payload) {
        String name = payload.get("name");
        String description = payload.get("description");
        return communityService.createCommunity(name, description);
    }

    // Listele: GET /api/communities
    @GetMapping
    public List<Community> getAllCommunities() {
        return communityService.getAllCommunities();
    }
}