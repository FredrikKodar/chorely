package com.fredrikkodar.chorely.testUtils;

import com.fredrikkodar.chorely.dto.AuthResponse;
import org.springframework.boot.resttestclient.TestRestTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;

public class TestUtils {

    public static final String FIRST_NAME = "FirstName";

    public static final String LAST_NAME = "LastName";

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
                                    String firstName,
                                    String lastName) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<String> request = new HttpEntity<>(
                String.format("{\"email\": \"%s\", \"password\": \"%s\", \"firstName\": \"%s\", \"lastName\": \"%s\"}", email, password, firstName, lastName), headers
        );
        testRestTemplate.postForEntity("http://localhost:" + port + "/api/auth/register",
                request,
                String.class);
    }

}