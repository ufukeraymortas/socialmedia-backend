package com.senate.socialmedia.controller;

import com.senate.socialmedia.Message;
import com.senate.socialmedia.service.MessageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/messages")
@CrossOrigin(origins = "*")
public class MessageController {

    @Autowired
    private MessageService messageService;

    // Mesaj Gönder: POST /api/messages
    @PostMapping
    public Message sendMessage(@RequestBody Map<String, Object> payload) {
        // JSON'dan verileri al (Long'a çevirirken dikkat etmeliyiz)
        Long senderId = Long.valueOf(payload.get("senderId").toString());
        Long receiverId = Long.valueOf(payload.get("receiverId").toString());
        String content = (String) payload.get("content");

        return messageService.sendMessage(senderId, receiverId, content);
    }

    // Sohbet Geçmişi: GET /api/messages/history?u1=5&u2=8
    @GetMapping("/history")
    public List<Message> getChatHistory(@RequestParam Long u1, @RequestParam Long u2) {
        return messageService.getChatHistory(u1, u2);
    }
}