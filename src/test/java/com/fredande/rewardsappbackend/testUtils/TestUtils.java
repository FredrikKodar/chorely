package com.fredande.rewardsappbackend.testUtils;

import com.fredande.rewardsappbackend.dto.AuthResponse;
import com.fredande.rewardsappbackend.enums.Role;
import org.springframework.boot.resttestclient.TestRestTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;

public class TestUtils {

    public static String getToken(TestRestTemplate testRestTemplate, Integer port, String email, String password) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<String> request = new HttpEntity<>(
                String.format("{\"email\": \"%s\", \"password\": \"%s\"}", email, password), headers
        );
        ResponseEntity<AuthResponse> response = testRestTemplate.postForEntity("http://localhost:" + port + "/api/auth/login",
                request,
                AuthResponse.class);

        assert response.getBody() != null;
        return response.getBody().getToken();
    }

    public static void registerUser(TestRestTemplate testRestTemplate,
                                    Integer port, String email,
                                    String password,
                                    Role role) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<String> request = new HttpEntity<>(
                String.format("{\"email\": \"%s\", \"password\": \"%s\", \"role\": \"%s\"}", email, password, role), headers
        );
        testRestTemplate.postForEntity("http://localhost:" + port + "/api/auth/register",
                request,
                String.class);
    }

}