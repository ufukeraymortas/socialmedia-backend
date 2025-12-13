package com.senate.socialmedia;

import jakarta.persistence.*;

@Entity
@Table(name = "communities")
public class Community {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private String name;

    @Column(length = 500)
    private String description;

    // YENİ: Topluluk İkonu ve Banner'ı
    private String iconUrl;
    private String bannerUrl;

    // YENİ: Gizlilik Ayarı (true = Herkese Açık, false = Özel/İzinli)
    private boolean isPublic = true; 

    // Getter ve Setter'lar
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public String getIconUrl() { return iconUrl; }
    public void setIconUrl(String iconUrl) { this.iconUrl = iconUrl; }

    public String getBannerUrl() { return bannerUrl; }
    public void setBannerUrl(String bannerUrl) { this.bannerUrl = bannerUrl; }

    public boolean isPublic() { return isPublic; }
    public void setPublic(boolean isPublic) { this.isPublic = isPublic; }
}