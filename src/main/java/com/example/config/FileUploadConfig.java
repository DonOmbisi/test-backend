package com.example.config;

import org.springframework.boot.web.servlet.MultipartConfigFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.util.unit.DataSize;
import org.springframework.web.multipart.MultipartResolver;
import org.springframework.web.multipart.support.StandardServletMultipartResolver;

import jakarta.servlet.MultipartConfigElement;

@Configuration
public class FileUploadConfig {

    @Bean
    public MultipartConfigElement multipartConfigElement() {
        MultipartConfigFactory factory = new MultipartConfigFactory();
        
        // Set maximum file size to 100MB
        factory.setMaxFileSize(DataSize.ofMegabytes(100));
        
        // Set maximum request size to 100MB
        factory.setMaxRequestSize(DataSize.ofMegabytes(100));
        
        // Set file size threshold to 2KB (files smaller than this will be stored in memory)
        factory.setFileSizeThreshold(DataSize.ofKilobytes(2));
        
        // Set location for temporary files
        factory.setLocation(System.getProperty("java.io.tmpdir"));
        
        return factory.createMultipartConfig();
    }

    @Bean
    public MultipartResolver multipartResolver() {
        return new StandardServletMultipartResolver();
    }
}
