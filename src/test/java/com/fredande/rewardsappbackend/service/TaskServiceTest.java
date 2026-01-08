package com.fredande.rewardsappbackend.service;

import com.fredande.rewardsappbackend.CustomUserDetails;
import com.fredande.rewardsappbackend.dto.TaskCreationRequest;
import com.fredande.rewardsappbackend.dto.TaskReadResponse;
import com.fredande.rewardsappbackend.dto.TaskSavedResponse;
import com.fredande.rewardsappbackend.dto.TaskUpdateRequest;
import com.fredande.rewardsappbackend.model.Task;
import com.fredande.rewardsappbackend.model.User;
import com.fredande.rewardsappbackend.repository.TaskRepository;
import com.fredande.rewardsappbackend.repository.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import org.apache.coyote.BadRequestException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static com.fredande.rewardsappbackend.enums.TaskStatus.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class TaskServiceTest {

    @Mock
    private TaskRepository taskRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private UserService userService;

    @InjectMocks
    private TaskService taskService;

    /**
     * A valid user and TaskCreationRequest should return DTO of TaskSavedResponse
     */
    @Test
    void create_TaskOnParent_valid() {
        // Arrange
        String title = "This is the title";
        String description = "Here is the description";
        Integer points = 10;
        User user = new User();
        user.setId(1);
        CustomUserDetails userDetails = new CustomUserDetails(user);
        TaskCreationRequest request = new TaskCreationRequest(title, description, points);

        // Act
        var response = taskService.createTaskOnParent(request, userDetails);

        //Assert
        assertInstanceOf(TaskSavedResponse.class, response);
        assertEquals(title, response.title());
        assertEquals(description, response.description());
        assertEquals(points, response.points());
        verify(taskRepository).save(any(Task.class));
    }

    @Test
    void get_AllTasksByUser_valid() {

        // Arrange
        String title = "This is the title";
        String description = "Here is the description";
        Integer points = 10;
        User user = new User();
        user.setId(1);
        User user2 = new User();
        user.setId(1);
        Task task1 = new Task();
        task1.setTitle(title);
        task1.setDescription(description);
        task1.setPoints(points);
        task1.setUser(user2);
        Task task2 = new Task();
        task2.setTitle(title);
        task2.setDescription(description);
        task2.setPoints(points);
        task2.setUser(user2);
        CustomUserDetails userDetails = new CustomUserDetails(user);
        when(userRepository.findById(any())).thenReturn(Optional.of(user));
        when(taskRepository.findByUser(user)).thenReturn(List.of(task1, task2));

        // Act
        var response = taskService.getAllTasksByUser(userDetails);

        // Assert
        assertInstanceOf(List.class, response);
        assertEquals(2, response.size());
        verify(userRepository).findById(any(Integer.class));
        verify(taskRepository).findByUser(any(User.class));
    }

    @Test
    void update_valid() {
        // Arrange
        String originalTitle = "This is the old title";
        String originalDescription = "Here is the old description";
        String updatedTitle = "This is the new title";
        String updatedDescription = "Here is the new description";
        Integer updatedPoints = 100;
        Integer points = 10;
        User user = new User();
        Integer userId = 1;
        user.setId(userId);
        Task task = new Task();
        Integer taskId = 1;
        task.setTitle(originalTitle);
        task.setDescription(originalDescription);
        task.setPoints(points);
        task.setId(taskId);
        task.setUser(user);
        CustomUserDetails userDetails = new CustomUserDetails(user);
        TaskUpdateRequest request = new TaskUpdateRequest(updatedTitle, updatedDescription, updatedPoints, APPROVED);
        when(taskRepository.findById(taskId)).thenReturn(Optional.of(task));
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));

        // Act
        var response = taskService.update(1, userDetails, request);

        //Assert
        assertInstanceOf(TaskReadResponse.class, response);
        assertEquals(updatedTitle, response.title());
        assertEquals(updatedDescription, response.description());
        assertEquals(updatedPoints, response.points());
        verify(taskRepository).findById(any(Integer.class));
        verify(userRepository).findById(any(Integer.class));
        verify(taskRepository).save(any(Task.class));
    }

    /**
     * A user should be able to toggle between ASSIGNED and PENDING_APPROVAL.
     */
    @Test
    void update_toggleStatus_valid() throws BadRequestException {
        // Arrange
        String originalTitle = "This is the title";
        String originalDescription = "Here is the description";
        Integer points = 10;
        User user = new User();
        Integer userId = 1;
        user.setId(userId);
        Task task = new Task();
        Integer taskId = 1;
        task.setTitle(originalTitle);
        task.setDescription(originalDescription);
        task.setPoints(points);
        task.setId(taskId);
        task.setUser(user);
        CustomUserDetails userDetails = new CustomUserDetails(user);
        when(taskRepository.findById(taskId)).thenReturn(Optional.of(task));
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));

        // Act
        var response = taskService.toggleStatus(1, userDetails);

        //Assert
        assertEquals(PENDING_APPROVAL, response.status());
        verify(taskRepository).findById(any(Integer.class));
        verify(userRepository).findById(any(Integer.class));
        verify(taskRepository).save(any(Task.class));
    }

    /**
     * If a user tries to toggle the status on a Task with APPROVED status, an Exception should be thrown.
     */
    @Test
    void update_toggleStatus_invalid_throwsBadRequestException() {
        // Arrange
        String originalTitle = "This is the title";
        String originalDescription = "Here is the description";
        Integer points = 10;
        User user = new User();
        Integer userId = 1;
        user.setId(userId);
        Task task = new Task();
        Integer taskId = 1;
        task.setTitle(originalTitle);
        task.setDescription(originalDescription);
        task.setPoints(points);
        task.setId(taskId);
        task.setUser(user);
        task.setStatus(APPROVED);
        CustomUserDetails userDetails = new CustomUserDetails(user);
        when(taskRepository.findById(taskId)).thenReturn(Optional.of(task));
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));

        // Act & Assert
        assertThrows(BadRequestException.class, () -> taskService.toggleStatus(1, userDetails));
        verify(taskRepository).findById(any(Integer.class));
        verify(userRepository).findById(any(Integer.class));
    }

    /**
     * A valid method call should return a TaskReadResponse DTO.
     */
    @Test
    void get_TaskByIdAndUser_valid() {
        // Arrange
        String title = "This is the title";
        String description = "Here is the description";
        Integer points = 10;
        Integer taskId = 4;
        User user = new User();
        user.setId(1);
        User user2 = new User();
        user.setId(1);
        Task task = new Task();
        task.setTitle(title);
        task.setDescription(description);
        task.setPoints(points);
        task.setUser(user2);
        CustomUserDetails userDetails = new CustomUserDetails(user);
        when(userRepository.findById(userDetails.getId())).thenReturn(Optional.of(user));
        when(taskRepository.findByIdAndUser(taskId, user)).thenReturn(Optional.of(task));

        // Act
        var response = taskService.getTaskByIdAndUser(taskId, userDetails);

        // Assert
        assertInstanceOf(TaskReadResponse.class, response);
        assertEquals(title, response.title());
        assertEquals(description, response.description());
        assertEquals(points, response.points());
        verify(userRepository).findById(any(Integer.class));
        verify(taskRepository).findByIdAndUser(any(Integer.class), any(User.class));
    }

    /**
     * Attempting to get a task that is related to another user should throw EntityNotFoundException.
     */
    @Test
    void get_TaskByIdAndUser_invalid_userTaskMismatch() {
        // Arrange
        Integer taskId = 4;
        User user1 = new User();
        user1.setId(1);
        CustomUserDetails userDetails = new CustomUserDetails(user1);
        when(userRepository.findById(userDetails.getId())).thenReturn(Optional.of(user1));
        when(taskRepository.findByIdAndUser(taskId, user1)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(EntityNotFoundException.class, () -> taskService.getTaskByIdAndUser(taskId, userDetails));
        verify(userRepository).findById(any(Integer.class));
        verify(taskRepository).findByIdAndUser(any(Integer.class), any(User.class));
    }

}