package com.lumiere.app.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

@Configuration
public class ChunkUploadConfig {

    @Value("${app.upload.temp-dir}")
    private String tempDir;

    @Value("${app.upload.final-dir}")
    private String finalDir;
    @Bean("tempPath")
    public Path tempUploadPath() throws IOException {
        Path path = Path.of(tempDir).toAbsolutePath().normalize();
        Files.createDirectories(path);
        return path;
    }

    @Bean("finalPath")
    public Path finalUploadPath() throws IOException {
        Path path =  Path.of(tempDir).toAbsolutePath().normalize();
        Files.createDirectories(path);
        return path;
    }
}
