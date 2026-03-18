package com.fredande.rewardsappbackend.controller;

import com.fredande.rewardsappbackend.TestcontainersConfig;
import com.fredande.rewardsappbackend.dto.LoginRequest;
import com.fredande.rewardsappbackend.enums.Role;
import com.fredande.rewardsappbackend.model.User;
import com.fredande.rewardsappbackend.repository.UserRepository;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.jdbc.test.autoconfigure.AutoConfigureTestDatabase;import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.context.annotation.Import;import org.springframework.http.MediaType;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import tools.jackson.databind.ObjectMapper;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Import(TestcontainersConfig.class)
@Transactional
class AuthenticationControllerIT {

    @Autowired
    private MockMvc mvc;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    /**
     * Valid login should return an AuthResponse object, consisting of a JWT, expires in time, a role and 200 OK.
     * In the arrange stage a user is saved to the database and a LoginRequest is created.
     * During Act a POST request is made to the login end-point using the LoginRequest object.
     * Lastly asserting that the response corresponds to what is expected.
     **/
    @Test
    void login_valid() throws Exception {
        // Arrange
        String email = "test@test.test";
        String password = "P@ss123456";
        User user = new User();
        user.setEmail(email);
        user.setPassword(passwordEncoder.encode(password));
        userRepository.save(user);

        LoginRequest loginRequest = new LoginRequest(email, password);

        // Act & Assert
        mvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").exists())
                .andExpect(jsonPath("$.expiresIn").value(86400L))
                .andExpect(jsonPath("$.roles").exists());
    }


    /**
     * Attempting to login with a non-existing email should return status 401 Unauthorized and a message.
     * In the arrange stage a user is saved to the database and a LoginRequest with an email not registered in the
     * database is created.
     * During Act a POST request is made to the login end-point using the LoginRequest object with the "bad" email..
     * Lastly asserting that the response is 401 Unauthorized and the message is the correct one.
     **/
    @Test
    void login_invalid_email_nonExisting() throws Exception {
        // Arrange
        String badEmail = "fest@test.test";
        String email = "test@test.test";
        String password = "P@ss123456";
        User user = new User();
        user.setEmail(email);
        user.setPassword(passwordEncoder.encode(password));
        user.setRole(Role.PARENT);
        userRepository.save(user);

        LoginRequest loginRequest = new LoginRequest(badEmail, password);

        // Act & Assert
        mvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginRequest)))
                .andExpect(status().isUnauthorized())
                .andExpect(content().string("Invalid email or password"));
    }


    /**
     * Attempting to login with an invalid password should return status 401 Unauthorized and a message.
     * In the arrange stage a user is saved to the database and a LoginRequest with a mismatching password is created.
     * During Act a POST request is made to the login end-point using the LoginRequest object with the "bad" password.
     * Lastly asserting that the response is 401 Unauthorized and the message is the correct one.
     **/
    @Test
    void login_invalid_password_wrong() throws Exception {
        // Arrange
        String invalidPassword = "password1234";
        String email = "test@test.test";
        String password = "P@ss123456";
        User user = new User();
        user.setEmail(email);
        user.setPassword(passwordEncoder.encode(password));
        user.setRole(Role.PARENT);
        userRepository.save(user);

        LoginRequest loginRequest = new LoginRequest(email, invalidPassword);

        // Act & Assert
        mvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginRequest)))
                .andExpect(status().isUnauthorized())
                .andExpect(content().string("Invalid email or password"));
    }

    /**
     * Attempting to login with an empty email should return status 401 Unauthorized and a message.
     * In the arrange stage a user is saved to the database and a LoginRequest with an empty email is created.
     * During Act a POST request is made to the login end-point using the LoginRequest object with the empty email.
     * Lastly asserting that the response is 401 Unauthorized and the message is the correct one.
     **/
    @Test
    void login_invalid_email_empty() throws Exception {
        // Arrange
        String email = "test@test.test";
        String password = "P@ss123456";
        User user = new User();
        user.setEmail(email);
        user.setPassword(passwordEncoder.encode(password));
        user.setRole(Role.PARENT);
        userRepository.save(user);

        LoginRequest loginRequest = new LoginRequest("", password);

        // Act & Assert
        mvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginRequest)))
                .andExpect(status().isUnauthorized())
                .andExpect(content().string("Invalid email or password"));
    }


    /**
     * Attempting to login with an empty password should return status 401 Unauthorized and a message.
     * In the arrange stage a user is saved to the database and a LoginRequest with an empty password is created.
     * During Act a POST request is made to the login end-point using the LoginRequest object with the empty password.
     * Lastly asserting that the response is 401 Unauthorized and the message is the correct one.
     **/
    @Test
    void login_invalid_password_empty() throws Exception {
        // Arrange
        String email = "test@test.test";
        String password = "P@ss123456";
        User user = new User();
        user.setEmail(email);
        user.setPassword(passwordEncoder.encode(password));
        user.setRole(Role.PARENT);
        userRepository.save(user);

        LoginRequest loginRequest = new LoginRequest(email, "");

        // Act & Assert
        mvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginRequest)))
                .andExpect(status().isUnauthorized())
                .andExpect(content().string("Invalid email or password"));
    }

    /**
     * Registering with a valid email and password should return 201.
     */
    @Test
    void register_Parent_valid() throws Exception {
        // Arrange
        String email = "test@test.test";
        String password = "P@ss123456";
        LoginRequest loginRequest = new LoginRequest(email, password);


        // Act & Assert
        mvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginRequest)))
                .andExpect(status().isCreated())
                .andExpect(content().string("User registered successfully"));
    }

    /**
     * Registering with an invalid email and password should return 400.
     * Malformed email where the top level domain is missing
     */
    @Test
    void register_Parent_invalid_email_noTLD() throws Exception {
        // Arrange
        String email = "test@test";
        String password = "P@ss123456";
        LoginRequest loginRequest = new LoginRequest(email, password);


        // Act & Assert
        mvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginRequest)))
                .andExpect(status().is(400))
                .andExpect(content().string("Malformed email address"));
    }

    /**
     * Registering with an invalid email and password should return 400.
     * Malformed email where the user is missing
     */
    @Test
    void register_Parent_invalid_email_noUser() throws Exception {
        // Arrange
        String email = "@test.test";
        String password = "P@ss123456";
        LoginRequest loginRequest = new LoginRequest(email, password);


        // Act & Assert
        mvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginRequest)))
                .andExpect(status().is(400))
                .andExpect(content().string("Malformed email address"));
    }

    /**
     * Registering with an invalid email and password should return 400.
     * Malformed email where the domain name is missing
     */
    @Test
    void register_Parent_invalid_email_noDomain() throws Exception {
        // Arrange
        String email = "test@.test";
        String password = "P@ss123456";
        LoginRequest loginRequest = new LoginRequest(email, password);


        // Act & Assert
        mvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginRequest)))
                .andExpect(status().is(400))
                .andExpect(content().string("Malformed email address"));
    }

    /**
     * Registering with an invalid email and password should return 400.
     * Malformed email where the at sign is missing
     */
    @Test
    void register_Parent_invalid_email_noAtSign() throws Exception {
        // Arrange
        String email = "testtest.test";
        String password = "P@ss123456";
        LoginRequest loginRequest = new LoginRequest(email, password);


        // Act & Assert
        mvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginRequest)))
                .andExpect(status().is(400))
                .andExpect(content().string("Malformed email address"));
    }

    /**
     * Registering with an invalid email and password should return 400.
     * Malformed email where the punctuation between domain and top level domain is missing
     */
    @Test
    void register_Parent_invalid_email_noPunctuationBeforeTLD() throws Exception {
        // Arrange
        String email = "test@testtest";
        String password = "P@ss123456";
        LoginRequest loginRequest = new LoginRequest(email, password);


        // Act & Assert
        mvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginRequest)))
                .andExpect(status().is(400))
                .andExpect(content().string("Malformed email address"));
    }

    /**
     * Registering with an invalid email and password should return 400.
     * Malformed email using a character that is not permitted.
     */
    @Test
    void register_Parent_invalid_email_unpermittedCharacter() throws Exception {
        // Arrange
        String email = "|test@testtest";
        String password = "P@ss123456";
        LoginRequest loginRequest = new LoginRequest(email, password);


        // Act & Assert
        mvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginRequest)))
                .andExpect(status().is(400))
                .andExpect(content().string("Malformed email address"));
    }


    /**
     * Registering with an invalid email and password should return 400.
     * Malformed email using a character that is not permitted.
     */
    @Test
    void register_Parent_invalid_password_tooShort() throws Exception {
        // Arrange
        String email = "test@test.test";
        String password = "P@ss123";
        LoginRequest loginRequest = new LoginRequest(email, password);


        // Act & Assert
        mvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginRequest)))
                .andExpect(status().is(400))
                .andExpect(content().string("Password must be 8 to 40 characters"));
    }


    /**
     * Registering with an invalid email and password should return 400.
     * Malformed email using a character that is not permitted.
     */
    @Test
    void register_Parent_invalid_password_tooLong() throws Exception {
        // Arrange
        String email = "test@testtest";
        String password = "P@ss123456P@ss123456P@ss123456P@ss123456";
        LoginRequest loginRequest = new LoginRequest(email, password);


        // Act & Assert
        mvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginRequest)))
                .andExpect(status().is(400))
                .andExpect(content().string("Malformed email address"));
    }

    /**
     * Registering with empty email should return 400.
     */
    @Test
    void register_Parent_invalid_email_missing() throws Exception {
        // Arrange
        String email = "";
        String password = "P@ss123456";
        LoginRequest loginRequest = new LoginRequest(email, password);


        // Act & Assert
        mvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginRequest)))
                .andExpect(status().is(400))
                .andExpect(content().string("Malformed email address"));
    }

    /**
     * Registering with empty password should return 400.
     */
    @Test
    void register_Parent_invalid_password_missing() throws Exception {
        // Arrange
        String email = "test@test.test";
        String password = "";
        LoginRequest loginRequest = new LoginRequest(email, password);


        // Act & Assert
        mvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginRequest)))
                .andExpect(status().is(400))
                .andExpect(content().string("Password must be 8 to 40 characters"));
    }

}