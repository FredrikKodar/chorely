package com.fredande.rewardsappbackend.controller;

import com.fredande.rewardsappbackend.TestcontainersConfig;
import com.fredande.rewardsappbackend.dto.UserResponse;
import com.fredande.rewardsappbackend.enums.Role;
import com.fredande.rewardsappbackend.model.User;
import com.fredande.rewardsappbackend.repository.UserRepository;
import com.fredande.rewardsappbackend.testUtils.TestUtils;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.resttestclient.TestRestTemplate;
import org.springframework.boot.resttestclient.autoconfigure.AutoConfigureTestRestTemplate;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.context.annotation.Import;
import org.springframework.http.*;
import org.springframework.test.context.ActiveProfiles;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
@AutoConfigureTestRestTemplate
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@Import(TestcontainersConfig.class)
class UserControllerIT {

    private final String VALID_EMAIL = "test@test.test";
    private final String VALID_PASSWORD = "P@ss1234";

    @Autowired
    private TestRestTemplate testRestTemplate;

    @LocalServerPort
    private Integer port;

    @Autowired
    UserRepository userRepository;

    @BeforeAll
    void beforeAll() {
        TestUtils.registerUser(testRestTemplate, port, VALID_EMAIL, VALID_PASSWORD, Role.PARENT); // Create a user in the database, to use in the tests
    }

    // With JWT token generation and validation

    /**
     * Log in as a registered user, obtain a JWT token and attempt to get user info.
     *
     * @Expected: ResponseEntity of type UserResponse and status code 200 OK.
     */
    @Test
    void withValidJWT_valid_get() {
        // Arrange
        String token = TestUtils.getToken(testRestTemplate, port, VALID_EMAIL, VALID_PASSWORD);
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + token);
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<String> request = new HttpEntity<>(headers);
        User user = userRepository.findByEmail(VALID_EMAIL).orElseThrow();

        // Act
        ResponseEntity<UserResponse> response =
                testRestTemplate.exchange(
                        "http://localhost:" + port + "/api/users/" + user.getId(),
                        HttpMethod.GET,
                        request,
                        UserResponse.class);

        // Assert
        assert response.getBody() != null;
        System.out.println(response.getBody().email());
        assertEquals(VALID_EMAIL, response.getBody().email());

    }

    /**
     * Log in as a registered user, obtain a JWT token and attempt to get another user's info.
     *
     * @Expected: Status code 400 Bad Request.
     */
    @Test
    void withValidJWT_invalid_get_invalidId() {
        // Arrange
        String token = TestUtils.getToken(testRestTemplate, port, VALID_EMAIL, VALID_PASSWORD);
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + token);
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<String> request = new HttpEntity<>(headers);
        User user = userRepository.findByEmail(VALID_EMAIL).orElseThrow();

        // Act
        ResponseEntity<?> response =
                testRestTemplate.exchange(
                        "http://localhost:" + port + "/api/users/" + (user.getId() - 1),
                        HttpMethod.GET,
                        request,
                        UserResponse.class);

        // Assert
        assertEquals(401, response.getStatusCode().value());

    }

}