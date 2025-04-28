package com.bytebandit.gateway.config;

import com.google.api.client.googleapis.auth.oauth2.GoogleIdTokenVerifier;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.gson.GsonFactory;
import java.util.Collections;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class GoogleAuthConfig {
    @Value("${google.oauth.client-id}") // Get from application properties
    private String googleClientId;
    
    /**
     * Creates a GoogleIdTokenVerifier bean for verifying Google ID tokens.
     *
     * @return GoogleIdTokenVerifier instance.
     */
    @Bean
    public GoogleIdTokenVerifier googleIdTokenVerifier() {
        return new GoogleIdTokenVerifier.Builder(
            new NetHttpTransport(),
            new GsonFactory())
            .setAudience(Collections.singletonList(googleClientId))
            .setIssuer("https://accounts.google.com")
            .build();
    }
}
