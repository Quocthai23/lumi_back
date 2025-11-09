// com.lumiere.app.config.MediaConfig
package com.lumiere.app.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.nio.file.Files;
import java.nio.file.Path;

@Configuration
public class MediaConfig {

    @Bean
    public Path mediaRoot(@Value("${spring.media.root}") String root) throws Exception {
        Path p = Path.of(root).toAbsolutePath().normalize();
        Files.createDirectories(p.resolve("attachments"));
        return p;
    }
}
