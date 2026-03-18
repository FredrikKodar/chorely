package com.fredande.rewardsappbackend.controller;

import com.fredande.rewardsappbackend.TestcontainersConfig;
import com.fredande.rewardsappbackend.config.CustomUserDetails;
import com.fredande.rewardsappbackend.dto.*;
import com.fredande.rewardsappbackend.model.User;
import com.fredande.rewardsappbackend.testUtils.TestUtils;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.jdbc.test.autoconfigure.AutoConfigureTestDatabase;import org.springframework.boot.resttestclient.TestRestTemplate;
import org.springframework.boot.resttestclient.autoconfigure.AutoConfigureTestRestTemplate;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.context.annotation.Import;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.*;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import tools.jackson.databind.ObjectMapper;

import java.util.List;

import static com.fredande.rewardsappbackend.enums.Role.PARENT;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
@AutoConfigureTestRestTemplate
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@Import(TestcontainersConfig.class)
class TaskControllerIT {

    private final String VALID_TITLE = "This is the title";
    private final String VALID_DESCRIPTION = "Here is the description";
    private final Integer VALID_POINTS = 10;
    private final String INVALID_STRING_EMPTY = "";
    private final Integer INVALID_POINTS_NEGATIVE = -10;
    private final String VALID_EMAIL = "test@test.test";
    private final String VALID_PASSWORD = "P@ss1234";

    @Autowired
    private TestRestTemplate testRestTemplate;

    @LocalServerPort
    private Integer port;

    @Autowired
    MockMvc mvc;

    @Autowired
    ObjectMapper objectMapper;

    @BeforeAll
    void beforeAll() {
        TestUtils.registerUser(testRestTemplate, port, VALID_EMAIL, VALID_PASSWORD, PARENT); // Create a user in the database, to use in the tests
    }

    // With JWT token generation and validation

    /**
     * Log in as a registered user, obtain a JWT token and post a new task.
     *
     * @Expected: ResponseEntity of type TaskSavedResponse and status code 201 Created.
     */
    @Test
    void withValidJWT_create_valid() {
        // Arrange
        String token = TestUtils.getToken(testRestTemplate, port, VALID_EMAIL, VALID_PASSWORD);
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + token);
        headers.setContentType(MediaType.APPLICATION_JSON);
        TaskCreationRequest createdTask = new TaskCreationRequest(
                VALID_TITLE,
                VALID_DESCRIPTION,
                VALID_POINTS);
        HttpEntity<String> request = new HttpEntity<>(
                objectMapper.writeValueAsString(createdTask),
                headers);

        // Act
        ResponseEntity<TaskSavedResponse> response =
                testRestTemplate.exchange(
                        "http://localhost:" + port + "/api/tasks",
                        HttpMethod.POST,
                        request,
                        TaskSavedResponse.class);

        // Assert
        assertEquals(HttpStatus.CREATED, response.getStatusCode());
    }


    /**
     * Log in as a registered user, obtain a JWT token and get a list of the users tasks.
     *
     * @Expected: ResponseEntity of type List of TaskReadResponse and status code 200 OK.
     */
    @Test
    void withValidJWT_get_valid_allTasksByUser_returnsListOfTaskSavedResponse() {
        // Arrange
        String token = TestUtils.getToken(testRestTemplate, port, VALID_EMAIL, VALID_PASSWORD);
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + token);
        headers.setContentType(MediaType.APPLICATION_JSON);
        TaskCreationRequest createdTask = new TaskCreationRequest(
                VALID_TITLE,
                VALID_DESCRIPTION,
                VALID_POINTS);
        HttpEntity<String> postRequest = new HttpEntity<>(
                objectMapper.writeValueAsString(createdTask),
                headers);
        HttpEntity<String> getRequest = new HttpEntity<>(
                headers);
        ParameterizedTypeReference<List<TaskReadResponse>> responseList = new ParameterizedTypeReference<>() {
        };

        // Act
        ResponseEntity<TaskSavedResponse> firstPostResponse =
                testRestTemplate.exchange(
                        "http://localhost:" + port + "/api/tasks",
                        HttpMethod.POST,
                        postRequest,
                        TaskSavedResponse.class);
        ResponseEntity<TaskSavedResponse> secondPostResponse =
                testRestTemplate.exchange(
                        "http://localhost:" + port + "/api/tasks",
                        HttpMethod.POST,
                        postRequest,
                        TaskSavedResponse.class);
        ResponseEntity<List<TaskReadResponse>> getResponse =
                testRestTemplate.exchange(
                        "http://localhost:" + port + "/api/tasks",
                        HttpMethod.GET,
                        getRequest,
                        responseList);

        // Assert
        assertEquals(HttpStatus.CREATED, firstPostResponse.getStatusCode());
        assertEquals(HttpStatus.CREATED, secondPostResponse.getStatusCode());
        assertEquals(HttpStatus.OK, getResponse.getStatusCode());
        assert getResponse.getBody() != null;
        assertEquals(VALID_TITLE, getResponse.getBody().getFirst().title());
    }

    /**
     * Login a registered user, obtain a JWT token and get a task by id.
     *
     * @Expected: ResponseEntity of type TaskReadResponse and status code 200.
     */
    @Test
    void withValidJWT_get_valid_taskByIdAndUser_returnsTaskSavedResponse() {
        // Arrange
        String token = TestUtils.getToken(testRestTemplate, port, VALID_EMAIL, VALID_PASSWORD);
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + token);
        headers.setContentType(MediaType.APPLICATION_JSON);
        TaskCreationRequest createdTask = new TaskCreationRequest(
                VALID_TITLE,
                VALID_DESCRIPTION,
                VALID_POINTS);
        HttpEntity<String> postRequest = new HttpEntity<>(
                objectMapper.writeValueAsString(createdTask),
                headers);
        HttpEntity<String> getRequest = new HttpEntity<>(
                headers);

        // Act
        ResponseEntity<TaskSavedResponse> postResponse =
                testRestTemplate.exchange(
                        "http://localhost:" + port + "/api/tasks",
                        HttpMethod.POST,
                        postRequest,
                        TaskSavedResponse.class);

        assert postResponse.getBody() != null;
        ResponseEntity<TaskReadResponse> getResponse =
                testRestTemplate.exchange(
                        "http://localhost:" + port + "/api/tasks/" + postResponse.getBody().id(),
                        HttpMethod.GET,
                        getRequest,
                        TaskReadResponse.class);

        // Assert
        assertEquals(HttpStatus.CREATED, postResponse.getStatusCode());
        assertEquals(HttpStatus.OK, getResponse.getStatusCode());
        assert getResponse.getBody() != null;
        assertEquals(VALID_TITLE, getResponse.getBody().title());
    }


    /**
     * Log in as a registered user, obtain a JWT token and post a new task with a blank title.
     *
     * @Expected: ResponseEntity of type String and status code 400 Bad Request.
     */
    @Test
    void withValidJWT_create_invalid_title_empty_throwsBadRequestException() {
        // Arrange
        String token = TestUtils.getToken(testRestTemplate, port, VALID_EMAIL, VALID_PASSWORD);
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + token);
        headers.setContentType(MediaType.APPLICATION_JSON);
        TaskCreationRequest createdTask = new TaskCreationRequest(
                INVALID_STRING_EMPTY,
                VALID_DESCRIPTION,
                VALID_POINTS);
        HttpEntity<String> request = new HttpEntity<>(
                objectMapper.writeValueAsString(createdTask),
                headers);

        // Act
        ResponseEntity<String> response =
                testRestTemplate.exchange(
                        "http://localhost:" + port + "/api/tasks",
                        HttpMethod.POST,
                        request,
                        String.class);

        // Assert
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    }


    /**
     * Log in as a registered user, obtain a JWT token and post a new task with negative points value.
     *
     * @Expected: ResponseEntity of type String and status code 400 Bad Request.
     */
    @Test
    void withValidJWT_create_invalid_points_negative_throwsBadRequestException() {
        // Arrange
        String token = TestUtils.getToken(testRestTemplate, port, VALID_EMAIL, VALID_PASSWORD);
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + token);
        headers.setContentType(MediaType.APPLICATION_JSON);
        TaskCreationRequest createdTask = new TaskCreationRequest(
                VALID_TITLE,
                VALID_DESCRIPTION,
                INVALID_POINTS_NEGATIVE);
        HttpEntity<String> request = new HttpEntity<>(
                objectMapper.writeValueAsString(createdTask),
                headers);

        // Act
        ResponseEntity<?> response =
                testRestTemplate.exchange(
                        "http://localhost:" + port + "/api/tasks",
                        HttpMethod.POST,
                        request,
                        String.class);

        // Assert
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    }


    /**
     * Log in as a registered user, obtain a JWT token, create a task and attempt to update the task with a valid
     * points value.
     *
     * @Expected: ResponseEntity of type TaskReadResponse and status code 201 Created.
     */
    @Test
    void withValidJWT_update_valid_returnsTaskReadResponse() {
        // Arrange
        String token = TestUtils.getToken(testRestTemplate, port, VALID_EMAIL, VALID_PASSWORD);
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + token);
        headers.setContentType(MediaType.APPLICATION_JSON);
        TaskCreationRequest createdTask = new TaskCreationRequest(
                VALID_TITLE,
                VALID_DESCRIPTION,
                VALID_POINTS);
        HttpEntity<String> creationRequest = new HttpEntity<>(
                objectMapper.writeValueAsString(createdTask),
                headers);
        HttpEntity<String> updateRequest = new HttpEntity<>(
                String.format("{\"points\": %d}",
                        VALID_POINTS + 10),
                headers);

        // Act
        ResponseEntity<TaskSavedResponse> creationResponse =
                testRestTemplate.exchange(
                        "http://localhost:" + port + "/api/tasks",
                        HttpMethod.POST,
                        creationRequest,
                        TaskSavedResponse.class);

        assert creationResponse.getBody() != null;
        ResponseEntity<TaskReadResponse> updateResponse =
                testRestTemplate.exchange(
                        "http://localhost:" + port + "/api/tasks/" + creationResponse.getBody().id(),
                        HttpMethod.PATCH,
                        updateRequest,
                        TaskReadResponse.class);

        // Assert
        assertEquals(HttpStatus.CREATED, creationResponse.getStatusCode());
        assertEquals(HttpStatus.CREATED, updateResponse.getStatusCode());
    }


    /**
     * Log in as a registered user, obtain a JWT token, create a task and attempt to update the task with a negative
     * points value.
     *
     * @Expected: ResponseEntity of type String and status code 400 Bad Request.
     */
    @Test
    void withValidJWT_update_invalid_points_negativeNumber_throwsBadRequestException() {
        // Arrange
        String token = TestUtils.getToken(testRestTemplate, port, VALID_EMAIL, VALID_PASSWORD);
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + token);
        headers.setContentType(MediaType.APPLICATION_JSON);
        TaskCreationRequest createdTask = new TaskCreationRequest(
                VALID_TITLE,
                VALID_DESCRIPTION,
                VALID_POINTS);
        HttpEntity<String> creationRequest = new HttpEntity<>(
                objectMapper.writeValueAsString(createdTask),
                headers);
        HttpEntity<String> updateRequest = new HttpEntity<>(
                String.format("{\"points\": %d}",
                        -10),
                headers);

        // Act
        ResponseEntity<TaskSavedResponse> creationResponse =
                testRestTemplate.exchange(
                        "http://localhost:" + port + "/api/tasks",
                        HttpMethod.POST,
                        creationRequest,
                        TaskSavedResponse.class);

        assert creationResponse.getBody() != null;
        ResponseEntity<?> updateResponse =
                testRestTemplate.exchange(
                        "http://localhost:" + port + "/api/tasks/" + creationResponse.getBody().id(),
                        HttpMethod.PATCH,
                        updateRequest,
                        String.class);

        // Assert
        assertEquals(HttpStatus.CREATED, creationResponse.getStatusCode());
        assertEquals(HttpStatus.BAD_REQUEST, updateResponse.getStatusCode());
    }


    /**
     * Log in as a registered user, obtain a JWT token, create a task and attempt to update the task with an empty title.
     *
     * @Expected: ResponseEntity of type String and status code 400 Bad Request.
     */
    @Test
    void withValidJWT_update_invalid_title_blank_throwsBadRequestException() {
        // Arrange
        String token = TestUtils.getToken(testRestTemplate, port, VALID_EMAIL, VALID_PASSWORD);
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + token);
        headers.setContentType(MediaType.APPLICATION_JSON);
        TaskCreationRequest createdTask = new TaskCreationRequest(
                VALID_TITLE,
                VALID_DESCRIPTION,
                VALID_POINTS);
        HttpEntity<String> creationRequest = new HttpEntity<>(
                objectMapper.writeValueAsString(createdTask),
                headers);
        HttpEntity<String> updateRequest = new HttpEntity<>(
                String.format("{\"title\": \"%s\"}",
                        INVALID_STRING_EMPTY),
                headers);

        // Act
        ResponseEntity<TaskSavedResponse> creationResponse =
                testRestTemplate.exchange(
                        "http://localhost:" + port + "/api/tasks",
                        HttpMethod.POST,
                        creationRequest,
                        TaskSavedResponse.class);

        assert creationResponse.getBody() != null;
        ResponseEntity<?> updateResponse =
                testRestTemplate.exchange(
                        "http://localhost:" + port + "/api/tasks/" + creationResponse.getBody().id(),
                        HttpMethod.PATCH,
                        updateRequest,
                        String.class);

        // Assert
        assertEquals(HttpStatus.CREATED, creationResponse.getStatusCode());
        assertEquals(HttpStatus.BAD_REQUEST, updateResponse.getStatusCode());
    }

    // With mock user. Without JWT token creation and validation.

    /**
     * Create a new task with a mock user. Does not involve JWT creation and validation.
     *
     * @throws Exception BadRequestException
     * @Expected: Status 201 Created.
     */
    @WithMockUser
    @Test
    void withMockUser_create_valid() throws Exception {
        // Arrange
        User user = new User();
        user.setRole(PARENT);
        CustomUserDetails userDetails = new CustomUserDetails(user);
        TaskCreationRequest request = new TaskCreationRequest(
                VALID_TITLE,
                VALID_DESCRIPTION,
                VALID_POINTS);
        Authentication authentication = new UsernamePasswordAuthenticationToken(
                userDetails, null, userDetails.getAuthorities());

        // Act & Assert
        mvc.perform(post("/api/tasks")
                        .with(SecurityMockMvcRequestPostProcessors.authentication(authentication))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated());
    }

    /**
     * Attempt to create a new task with a mock user. The title is empty, which triggers the validation of the DTO.
     * Does not involve JWT creation and validation.
     *
     * @throws Exception BadRequestException
     * @Expected: Status 400 Bad Request.
     */
    @WithMockUser
    @Test
    void withMockUser_create_invalid_title_empty_throwsBadRequestException() throws Exception {
        // Arrange
        User user = new User();
        CustomUserDetails userDetails = new CustomUserDetails(user);
        TaskCreationRequest request = new TaskCreationRequest(
                INVALID_STRING_EMPTY,
                VALID_DESCRIPTION,
                VALID_POINTS);
        Authentication authentication = new UsernamePasswordAuthenticationToken(
                userDetails, null, userDetails.getAuthorities());

        // Act & Assert
        mvc.perform(post("/api/tasks")
                        .with(SecurityMockMvcRequestPostProcessors.authentication(authentication))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());

    }

    /**
     * Attempt to create a new task with a mock user. The description is empty, which triggers the validation of the DTO.
     * Does not involve JWT creation and validation.
     *
     * @throws Exception BadRequestException
     * @Expected: Status 400 Bad Request.
     */
    @WithMockUser
    @Test
    void withMockUser_create_invalid_description_empty_throwsBadRequestException() throws Exception {
        // Arrange
        User user = new User();
        CustomUserDetails userDetails = new CustomUserDetails(user);
        TaskCreationRequest request = new TaskCreationRequest(
                VALID_TITLE,
                INVALID_STRING_EMPTY,
                VALID_POINTS);
        Authentication authentication = new UsernamePasswordAuthenticationToken(
                userDetails, null, userDetails.getAuthorities());

        // Act & Assert
        mvc.perform(post("/api/tasks")
                        .with(SecurityMockMvcRequestPostProcessors.authentication(authentication))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());

    }

    /**
     * Attempt to create a new task with a mock user. The points value is negative, which triggers the validation of the DTO.
     * Does not involve JWT creation and validation.
     *
     * @throws Exception BadRequestException
     * @Expected: ResponseEntity of type String and status code 400 Bad Request.
     */
    @WithMockUser
    @Test
    void withMockUser_create_invalid_points_negativeNumber_throwsBadRequestException() throws Exception {
        // Arrange
        User user = new User();
        CustomUserDetails userDetails = new CustomUserDetails(user);
        TaskCreationRequest request = new TaskCreationRequest(
                VALID_TITLE,
                VALID_DESCRIPTION,
                INVALID_POINTS_NEGATIVE);
        Authentication authentication = new UsernamePasswordAuthenticationToken(
                userDetails, null, userDetails.getAuthorities());

        // Act & Assert
        mvc.perform(post("/api/tasks")
                        .with(SecurityMockMvcRequestPostProcessors.authentication(authentication))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("Points must be a whole number of zero or greater"));

    }

    /**
     * Get all tasks for a mock user. Does not involve JWT creation and validation.
     *
     * @throws Exception BadRequestException
     * @Expected: ResponseEntity of type List of TaskReadResponse and status code 200 OK.
     */
    @WithMockUser
    @Test
    void withMockUser_get_valid_allTasksByUser_returnsListOfTaskSavedResponse() throws Exception {
        // Arrange
        User user = new User();
        user.setId(1);
        user.setRole(PARENT);
        CustomUserDetails userDetails = new CustomUserDetails(user);
        TaskCreationRequest request = new TaskCreationRequest(VALID_TITLE, VALID_DESCRIPTION, VALID_POINTS);
        Authentication authentication = new UsernamePasswordAuthenticationToken(
                userDetails, null, userDetails.getAuthorities());

        // Act
        mvc.perform(post("/api/tasks")
                        .with(SecurityMockMvcRequestPostProcessors.authentication(authentication))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated());
        mvc.perform(post("/api/tasks")
                        .with(SecurityMockMvcRequestPostProcessors.authentication(authentication))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated());

        // Assert
        mvc.perform(get("/api/tasks")
                        .with(SecurityMockMvcRequestPostProcessors.authentication(authentication))
                        .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isOk());
    }

    /**
     * Get all tasks for a mock user. Does not involve JWT creation and validation.
     *
     * @throws Exception BadRequestException
     * @Expected: ResponseEntity of type List of TaskReadResponse and status code 200 OK.
     */
    @WithMockUser
    @Test
    void withMockUser_get_valid_taskByIdAndUser_returnsTaskReadResponse() throws Exception {
        // Arrange
        User user = new User();
        user.setId(1);
        user.setRole(PARENT);
        CustomUserDetails userDetails = new CustomUserDetails(user);
        TaskCreationRequest request = new TaskCreationRequest(VALID_TITLE, VALID_DESCRIPTION, VALID_POINTS);
        Authentication authentication = new UsernamePasswordAuthenticationToken(
                userDetails, null, userDetails.getAuthorities());

        // Act
        var response = mvc.perform(post("/api/tasks")
                        .with(SecurityMockMvcRequestPostProcessors.authentication(authentication))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated()).andReturn();

        TaskSavedResponse readResponse = objectMapper.readValue(response.getResponse().getContentAsString(), TaskSavedResponse.class);

        // Assert
        mvc.perform(get("/api/tasks/" + readResponse.id())
                        .with(SecurityMockMvcRequestPostProcessors.authentication(authentication))
                        .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title").value(VALID_TITLE))
                .andExpect(jsonPath("$.description").value(VALID_DESCRIPTION))
                .andExpect(jsonPath("$.points").value(VALID_POINTS));
    }


}