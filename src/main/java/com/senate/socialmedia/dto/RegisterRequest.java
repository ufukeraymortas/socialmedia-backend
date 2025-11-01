package com.senate.socialmedia.dto;

//Bu sınıf, sadece JSON'dan veri almak için kullanılır.
//Alan adları (username, password) JSON'daki anahtarlarla (key) eşleşmelidir.
public class RegisterRequest {

 private String username;
 private String password;

 // Jackson (Spring'in kullandığı JSON kütüphanesi) bu metotlara ihtiyaç duyar.
 // Eclipse'te otomatik oluşturun: Sağ Tık -> Source -> Generate Getters and Setters...

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