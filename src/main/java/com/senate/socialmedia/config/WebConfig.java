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
        // "/uploads/resim.jpg" isteği gelirse...
        registry.addResourceHandler("/uploads/**")
                // ...git "uploads" klasörüne bak. ("file:" demek diskten oku demek)
                .addResourceLocations("file:./uploads/");
    }
}