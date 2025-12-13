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
    private String title;             // Kullanıcının ünvanı (örn: "Yazılımcı")
    private String bio;   
    private String profilePictureUrl; 
    private String headerUrl;
   
    public String getProfilePictureUrl() {
		return profilePictureUrl;
	}

	public void setProfilePictureUrl(String profilePictureUrl) {
		this.profilePictureUrl = profilePictureUrl;
	}
	
	public String getHeaderUrl() {
		return headerUrl; 
	}
	
    public void setHeaderUrl(String headerUrl) {
    	this.headerUrl = headerUrl; 
    }

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getBio() {
		return bio;
	}

	public void setBio(String bio) {
		this.bio = bio;
	}

    public User() {
    }

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
