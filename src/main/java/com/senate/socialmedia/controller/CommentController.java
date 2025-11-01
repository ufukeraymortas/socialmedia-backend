package com.senate.socialmedia.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.senate.socialmedia.Comment;
import com.senate.socialmedia.service.CommentService;

@RestController
@RequestMapping("/api/posts/{postId}/comments") // BÜTÜN ADRESLER BU ŞEKİLDE BAŞLAYACAK
public class CommentController {

    @Autowired
    private CommentService commentService;

    // --- 1. YENİ YORUM OLUŞTURMA API'Sİ ---
    // Adres: POST /api/posts/123/comments (123 = postId)
    @PostMapping
    public Comment createComment(
            @PathVariable Long postId, 
            @RequestParam("content") String content,
            @RequestParam("authorId") Long authorId,
            @RequestParam(value = "file", required = false) MultipartFile file,

            // YENİ EKLENEN OPSİYONEL PARAMETRE
            @RequestParam(value = "parentCommentId", required = false) Long parentCommentId) {

        // Servise bu yeni 'parentCommentId'yi de paslıyoruz
        return commentService.createComment(content, authorId, postId, file, parentCommentId);
    }


    // --- 2. YORUMLARI LİSTELEME API'Sİ ---
    // Adres: GET /api/posts/123/comments (123 = postId)
    @GetMapping
    public List<Comment> getCommentsForPost(@PathVariable Long postId) {
        // Servise "Bana bu postun yorumlarını ver" diyoruz
        return commentService.getCommentsForPost(postId);
    }
}