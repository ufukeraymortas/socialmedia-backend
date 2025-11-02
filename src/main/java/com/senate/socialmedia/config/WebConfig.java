package com.senate.socialmedia.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;

@Configuration
public class WebConfig implements WebMvcConfigurer {

	@Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/api/**")
                // Frontend URL'niz HTTP değil, HTTPS kullanıyor.
                // Lütfen aşağıdaki "senato-frontend.vercel.app" gibi adresi, 
                // Vercel'in size verdiği o gerçek adresle (URL'nizle) değiştirin.
                .allowedOrigins("https://senato.vercel.app") 
                .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS")
                .allowedHeaders("*");
    }

 @Override
 public void addResourceHandlers(ResourceHandlerRegistry registry) {
     // Eğer birisi /uploads/dosyaadi.jpg gibi bir URL isterse,
     // Spring'e "git ve ./uploads/ klasörünün içindeki
     // o dosyayı bul" diyoruz.
     registry.addResourceHandler("/uploads/**")
             .addResourceLocations("file:./uploads/");
 }
}