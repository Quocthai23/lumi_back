package com.lumiere.app.config;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import jakarta.annotation.PostConstruct;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;

import java.io.IOException;
import java.io.InputStream;


@Configuration
public class FirebaseConfig {
    @PostConstruct
    public void initialize() throws IOException {
        ClassPathResource resource = new ClassPathResource("firebase-config.json");
        InputStream inputStream = resource.getInputStream();
        FirebaseOptions options = FirebaseOptions.builder()
                    .setCredentials(GoogleCredentials.fromStream(inputStream))
                    .setStorageBucket("demofirebase-6e7a1.appspot.com")
                    .build();

            if (FirebaseApp.getApps().isEmpty()) {
                FirebaseApp.initializeApp(options);
            }
        }
}
