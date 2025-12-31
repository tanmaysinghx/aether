package com.aether.engine.common.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.nio.file.Path;
import java.nio.file.Paths;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Value("${app.storage.location:video_storage}")
    private String storageLocation;

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        Path path = Paths.get(storageLocation).toAbsolutePath().normalize();
        String resourcePath = "file:///" + path.toString().replace("\\", "/") + "/";

        // Serve HLS content from /api/v1/stream/** -> video_storage/hls/**
        registry.addResourceHandler("/api/v1/stream/**")
                .addResourceLocations(resourcePath + "hls/");
    }

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        // Allow all origins for development convenience
        registry.addMapping("/**")
                .allowedOrigins("*")
                .allowedMethods("*");
    }
}
