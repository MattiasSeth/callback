package com.example.callback.visma;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.*;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import java.util.Base64;

@RestController
@Slf4j
@RequiredArgsConstructor
public class VismaController {
        private String clientId = "challengenow1sandbox";
        private String clientSecret = "EA$;tc;kpskxRcR2udAGzedOZEQQztdPexFzs9pG7KQyeRJeHXRjmP0tqjfUvnV6";
        private String redirectUri = "https://localhost:44300/callback";
        private String tokenEndpoint = "https://identity.vismaonline.com/connect/token";
        private final RestTemplate restTemplate = new RestTemplate();

        @GetMapping("/callback")
        public ResponseEntity<String> handleCallback(
                @RequestParam("code") String code) {
        // Prepare headers for token request
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
        String authValue = Base64.getEncoder().encodeToString((clientId + ":" + clientSecret).getBytes());
        headers.set("Authorization", "Basic " + authValue);

        // Prepare body for token request
        MultiValueMap<String, String> body = new LinkedMultiValueMap<>();
        body.add("grant_type", "authorization_code");
        body.add("code", code);
        body.add("redirect_uri", redirectUri);

        HttpEntity<MultiValueMap<String, String>> requestEntity = new HttpEntity<>(body, headers);

        try {
            // Exchange code for access token
            ResponseEntity<String> response = restTemplate.exchange(
                    tokenEndpoint,
                    HttpMethod.POST,
                    requestEntity,
                    String.class
            );
            return ResponseEntity.ok(response.getBody()); // Return JSON token responsex
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error exchanging code for token: " + e.getMessage());
        }
    }
}