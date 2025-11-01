package com.senate.socialmedia.dto;

public class CreatePostRequest {

    private String content;
    private Long authorId; // Postu kimin attığını bilmek için

    // --- Otomatik Getters/Setters oluşturun ---

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public Long getAuthorId() {
        return authorId;
    }

    public void setAuthorId(Long authorId) {
        this.authorId = authorId;
    }
}