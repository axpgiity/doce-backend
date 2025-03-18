package com.doce_ai.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {
    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/api/**")  // Apply CORS to all /api endpoints
                //.allowedOrigins("http://localhost:4321","http://localhost:4322", "http://localhost:4323", "http://localhost:5000","http://localhost:8080")  // Allow frontend and Python script
                .allowedOriginPatterns("http://localhost:*")
                .allowedMethods("GET", "POST")  // Allow GET and POST methods
                .allowCredentials(true)  // Allow cookies, if needed
                .allowedHeaders("*");  // Allow all headers
    }
}
