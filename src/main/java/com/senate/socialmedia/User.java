package com.senate.socialmedia; 

// Gerekli kütüphaneleri içe aktarıyoruz (import)
// Bunlar, Spring Boot 3+ ile gelen 'jakarta' kütüphaneleridir.
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity // 1. ÖNEMLİ ANOTASYON: Spring'e bunun bir veritabanı tablosu olduğunu söyler.
@Table(name = "users") // 2. İsteğe bağlı: Tablo adını 'users' olarak belirler (yoksa sınıf adını kullanırdı).
public class User {

    @Id // 3. ÖNEMLİ ANOTASYON: Bu alanın 'Primary Key' (Birincil Anahtar) olduğunu söyler.
    @GeneratedValue(strategy = GenerationType.IDENTITY) // 4. ID'nin otomatik artmasını sağlar (1, 2, 3...)
    private Long id;

    private String username; // Kullanıcı adı için bir sütun
    private String password; // Şifre için bir sütun

    // (Gerçek bir uygulamada 'email', 'bio', 'profilResmi' gibi alanlar da eklenir)

    // Spring'in (daha doğrusu JPA'nın) bu sınıfı kullanabilmesi için 
    // boş bir constructor (yapıcı metot) gerekir.
    public User() {
    }

    // --- Getter ve Setter Metotları ---
    // 'private' alanlara dışarıdan erişmek için kullanılır.
    // Eclipse'te bunları otomatik oluşturabilirsiniz:
    // Kodun içinde boş bir yere sağ tıklayın -> Source -> Generate Getters and Setters... -> Select All -> Generate

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

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
