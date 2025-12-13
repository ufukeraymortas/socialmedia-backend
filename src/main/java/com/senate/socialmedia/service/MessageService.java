package com.senate.socialmedia.service;

import com.senate.socialmedia.Message;
import com.senate.socialmedia.MessageRepository;
import com.senate.socialmedia.User;
import com.senate.socialmedia.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class MessageService {

    @Autowired
    private MessageRepository messageRepository;

    @Autowired
    private UserRepository userRepository;

    // Mesaj Gönder
    public Message sendMessage(Long senderId, Long receiverId, String content) {
        User sender = userRepository.findById(senderId).orElseThrow(() -> new RuntimeException("Gönderen bulunamadı"));
        User receiver = userRepository.findById(receiverId).orElseThrow(() -> new RuntimeException("Alıcı bulunamadı"));

        Message msg = new Message();
        msg.setSender(sender);
        msg.setReceiver(receiver);
        msg.setContent(content);
        msg.setTimestamp(LocalDateTime.now());

        return messageRepository.save(msg);
    }

    // Sohbet Geçmişini Getir
    public List<Message> getChatHistory(Long userId1, Long userId2) {
        return messageRepository.findChatHistory(userId1, userId2);
    }
}