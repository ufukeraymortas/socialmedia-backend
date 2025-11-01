package com.senate.socialmedia.service;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.UUID; // Benzersiz isim oluşturmak için

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import jakarta.annotation.PostConstruct; // Sunucu başlarken çalışması için

@Service
public class FileStorageService {

    // application.properties dosyamızdan o ayarı buraya çekiyoruz
    @Value("${file.upload-dir}")
    private String uploadDir;

    private Path uploadPath; // ./uploads klasörünün tam yolu

    // Bu metot, sunucu başlar başlamaz çalışır
    @PostConstruct
    public void init() {
        try {
            // ./uploads dizinini temsil eden 'Path' nesnesini oluştur
            uploadPath = Paths.get(uploadDir);
            
            // Eğer ./uploads klasörü mevcut değilse, onu oluştur
            if (Files.notExists(uploadPath)) {
                Files.createDirectories(uploadPath);
            }
        } catch (IOException e) {
            // Klasör oluşturulamazsa sunucuyu çökert (çünkü bu kritik bir hata)
            throw new RuntimeException("Yükleme klasörü oluşturulamadı.", e);
        }
    }

    /**
     * Gelen bir dosyayı (fotoğrafı) diskteki 'uploads' klasörüne kaydeder.
     * @param file Tarayıcıdan gelen fotoğraf dosyası
     * @return Diske kaydedilen dosyanın YENİ, BENZERSİZ adı (örn: 123e4567-e89b-12d3-a456-426614174000.jpg)
     */
    public String storeFile(MultipartFile file) {
        // Orijinal dosya adını al (örn: tatil.jpg)
        String originalFileName = StringUtils.cleanPath(file.getOriginalFilename());

        try {
            // Dosya adı ".." gibi geçersiz karakterler içeriyorsa engelle (Güvenlik için)
            if (originalFileName.contains("..")) {
                throw new RuntimeException("Geçersiz dosya adı: " + originalFileName);
            }

            // Dosya uzantısını al (örn: ".jpg")
            String fileExtension = "";
            try {
                fileExtension = originalFileName.substring(originalFileName.lastIndexOf("."));
            } catch (Exception e) {
                // Uzantı yoksa boş kalsın
            }

            // YENİ, BENZERSİZ dosya adı oluştur
            // örn: "123e4567-e89b-12d3-a456-426614174000.jpg"
            String newFileName = UUID.randomUUID().toString() + fileExtension;

            // Dosyanın tam olarak nereye kaydedileceğini belirle
            // (örn: ./uploads/123e4567-e89b-12d3-a456-426614174000.jpg)
            Path targetLocation = this.uploadPath.resolve(newFileName);

            // Dosyayı Gelen yerden (InputStream) Hedef konuma (targetLocation) kopyala
            try (InputStream inputStream = file.getInputStream()) {
                Files.copy(inputStream, targetLocation, StandardCopyOption.REPLACE_EXISTING);
            }

            // Veritabanına kaydetmek için bu yeni, benzersiz adı geri döndür
            return newFileName;

        } catch (IOException ex) {
            throw new RuntimeException("Dosya kaydedilemedi: " + originalFileName, ex);
        }
    }
}