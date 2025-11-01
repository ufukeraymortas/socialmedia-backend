package com.senate.socialmedia.dto;

public class LoginRequest {

    private String username;
    private String password;

    // Eclipse'te otomatik oluşturun:
    // Sağ Tık -> Source -> Generate Getters and Setters... -> Select All
    
    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}