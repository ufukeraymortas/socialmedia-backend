package com.senate.socialmedia.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;

@Configuration
public class WebConfig implements WebMvcConfigurer {

	@Override
	public void addCorsMappings(CorsRegistry registry) {
	    registry.addMapping("/**") // Tüm yollara izin ver
	            .allowedOrigins("*") // HANGİ ADRESLERDEN İSTEK GELDİĞİ ÖNEMLİ DEĞİL (Vercel, Render, Localhost)
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