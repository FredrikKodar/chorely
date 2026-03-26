package com.fredrikkodar.chorely.service;

import com.fredrikkodar.chorely.config.CustomUserDetails;
import com.fredrikkodar.chorely.dto.TaskCreationRequest;
import com.fredrikkodar.chorely.dto.TaskSavedResponse;
import com.fredrikkodar.chorely.dto.TaskUpdateRequest;
import com.fredrikkodar.chorely.model.Task;
import com.fredrikkodar.chorely.model.User;
import com.fredrikkodar.chorely.repository.TaskRepository;
import com.fredrikkodar.chorely.repository.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static com.fredrikkodar.chorely.enums.TaskStatus.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

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
        User user = new User();
        user.setId(1);
        Task task1 = new Task();
        task1.setTitle("Title 1");
        task1.setDescription("Description 1");
        Task task2 = new Task();
        task2.setTitle("Title 2");
        task2.setDescription("Description 2");
        CustomUserDetails userDetails = new CustomUserDetails(user);
        when(userRepository.findById(1)).thenReturn(Optional.of(user));
        when(taskRepository.findByUser(user)).thenReturn(List.of(task1, task2));

        // Act
        var response = taskService.getAllTasksByUser(userDetails);

        // Assert
        assertEquals(2, response.size());
        verify(userRepository).findById(1);
        verify(taskRepository).findByUser(user);
    }

    @Test
    void update_valid() {
        // Arrange
        User user = new User();
        user.setId(1);
        CustomUserDetails userDetails = new CustomUserDetails(user);
        Task task = new Task();
        task.setTitle("This is the old title");
        task.setDescription("Here is the old description");
        task.setPoints(10);
        task.setId(1);
        task.setUser(user);
        TaskUpdateRequest request = new TaskUpdateRequest("This is the new title", "Here is the new description", 100, APPROVED);
        when(taskRepository.findById(task.getId())).thenReturn(Optional.of(task));
        when(userRepository.findById(user.getId())).thenReturn(Optional.of(user));

        // Act
        var response = taskService.update(1, userDetails, request);

        //Assert
        assertEquals("This is the new title", response.title());
        assertEquals("Here is the new description", response.description());
        assertEquals(100, response.points());
        assertEquals(APPROVED, response.status());
        verify(taskRepository).findById(1);
        verify(userRepository).findById(1);
        verify(taskRepository).save(task);
    }

    /**
     * A user should be able to toggle between ASSIGNED and PENDING_APPROVAL.
     */
    @Test
    void update_toggleStatus_valid() {
        // Arrange
        User user = new User();
        user.setId(1);
        CustomUserDetails userDetails = new CustomUserDetails(user);
        Task task = new Task();
        task.setTitle("This is the title");
        task.setDescription("Here is the description");
        task.setPoints(10);
        task.setId(1);
        task.setUser(user);
        task.setStatus(ASSIGNED);
        when(taskRepository.findById(task.getId())).thenReturn(Optional.of(task));
        when(userRepository.findById(user.getId())).thenReturn(Optional.of(user));

        // Act
        var response = taskService.toggleStatus(1, userDetails);

        //Assert
        assertEquals(PENDING_APPROVAL, response.status());
        verify(taskRepository).findById(1);
        verify(userRepository).findById(1);
        verify(taskRepository).save(task);
    }

    /**
     * If a Child role user tries to toggle the status on a Task with APPROVED status, an Exception should be thrown.
     */
    @Test
    void update_toggleStatus_invalid_throwsEntityNotFoundException() {
        // Arrange
        User user = new User();
        user.setId(1);
        CustomUserDetails userDetails = new CustomUserDetails(user);
        Task task = new Task();
        task.setTitle("This is the title");
        task.setDescription("Here is the description");
        task.setPoints(10);
        task.setId(1);
        task.setUser(user);
        task.setStatus(APPROVED);
        when(taskRepository.findById(task.getId())).thenReturn(Optional.of(task));
        when(userRepository.findById(user.getId())).thenReturn(Optional.of(user));

        // Act & Assert
        assertThrows(EntityNotFoundException.class, () -> taskService.toggleStatus(1, userDetails));
        verify(taskRepository).findById(1);
        verify(userRepository).findById(1);
    }

    /**
     * A valid method call should return a TaskReadResponse DTO.
     */
    @Test
    void get_TaskByIdAndUser_valid() {
        // Arrange
        User user = new User();
        user.setId(1);
        CustomUserDetails userDetails = new CustomUserDetails(user);
        Task task = new Task();
        task.setTitle("This is the title");
        task.setDescription("Here is the description");
        task.setPoints(10);
        task.setId(1);
        task.setUser(user);
        when(userRepository.findById(user.getId())).thenReturn(Optional.of(user));
        when(taskRepository.findByIdAndUser(task.getId(), user)).thenReturn(Optional.of(task));

        // Act
        var response = taskService.getTaskByIdAndUser(task.getId(), userDetails);

        // Assert
        assertEquals("This is the title", response.title());
        assertEquals("Here is the description", response.description());
        assertEquals(10, response.points());
        assertEquals(1, response.id());
        verify(userRepository).findById(1);
        verify(taskRepository).findByIdAndUser(1, user);
    }

    /**
     * Attempting to get a task that is related to another user should throw EntityNotFoundException.
     */
    @Test
    void get_TaskByIdAndUser_invalid_userTaskMismatch() {
        // Arrange
        Integer taskId = 4;
        User user = new User();
        user.setId(1);
        CustomUserDetails userDetails = new CustomUserDetails(user);
        when(userRepository.findById(userDetails.getId())).thenReturn(Optional.of(user));
        when(taskRepository.findByIdAndUser(taskId, user)).thenReturn(Optional.empty());

        // Act
        var exception = assertThrows(EntityNotFoundException.class, () -> taskService.getTaskByIdAndUser(taskId, userDetails));

        // Assert
        assertEquals("User-task mismatch", exception.getMessage());
        verify(userRepository).findById(any(Integer.class));
        verify(taskRepository).findByIdAndUser(any(Integer.class), any(User.class));
    }

    /**
     * Trying to change the status on a task when the user is not the creator should throw EntityNotFoundException.
     */
    @Test
    void update_approve_invalid_wrongUser() {
        // Arrange
        User user = new User();
        User createdBy = new User();
        User wrongUser = new User();
        user.setId(1);
        createdBy.setId(2);
        wrongUser.setId(3);
        CustomUserDetails userDetails = new CustomUserDetails(wrongUser);
        user.setTotalPoints(100);
        user.setCurrentPoints(10);
        Task task = new Task();
        task.setId(1);
        task.setUser(user);
        task.setPoints(10);
        task.setCreatedBy(createdBy);
        when(userRepository.findById(wrongUser.getId())).thenReturn(Optional.of(wrongUser));
        when(taskRepository.findByIdAndCreatedBy(task.getId(), wrongUser)).thenReturn(Optional.empty());

        // Act
        var exception = assertThrows(EntityNotFoundException.class, () -> taskService.approve(task.getId(), userDetails));

        // Assert
        assertEquals("User-task mismatch", exception.getMessage());
        verify(taskRepository).findByIdAndCreatedBy(any(Integer.class), any(User.class));
        verify(userRepository).findById(any(Integer.class));

    }

    /**
     * Setting the status to APPROVED should pass.
     */
    @Test
    void update_approve_valid() {
        // Arrange
        User user = new User();
        User createdBy = new User();
        user.setId(1);
        createdBy.setId(2);
        CustomUserDetails userDetails = new CustomUserDetails(createdBy);
        user.setTotalPoints(100);
        user.setCurrentPoints(10);
        Task task = new Task();
        task.setId(1);
        task.setUser(user);
        task.setPoints(10);
        task.setCreatedBy(createdBy);
        task.setTitle("Test title");
        task.setDescription("Test description");
        task.setStatus(PENDING_APPROVAL);
        ArgumentCaptor<Task> taskCaptor = ArgumentCaptor.forClass(Task.class);
        when(userRepository.findById(createdBy.getId())).thenReturn(Optional.of(createdBy));
        when(taskRepository.findByIdAndCreatedBy(task.getId(), createdBy)).thenReturn(Optional.of(task));

        // Act
        var response = taskService.approve(task.getId(), userDetails);

        // Assert
        verify(taskRepository).save(taskCaptor.capture());
        assertEquals(APPROVED, taskCaptor.getValue().getStatus());
        assertNotNull(response.updated());
        verify(taskRepository).findByIdAndCreatedBy(any(Integer.class), any(User.class));
        verify(userRepository).findById(any(Integer.class));

    }

    @Test
    void get_TasksPendingApproval_valid_onePendingApproval() {
        // Arrange
        User parent = new User();
        parent.setId(1);
        CustomUserDetails userDetails = new CustomUserDetails(parent);
        Task task = new Task();
        task.setStatus(PENDING_APPROVAL);
        task.setCreatedBy(parent);
        task.setTitle("title");
        task.setDescription("description");
        when(userRepository.findById(1)).thenReturn(Optional.of(parent));
        when(taskRepository.findAllByCreatedByAndStatus(parent, PENDING_APPROVAL)).thenReturn(List.of(task));

        // Act
        var response = taskService.getTasksPendingApproval(userDetails);

        // Assert
        assertEquals(1, response.size());
        assertEquals(PENDING_APPROVAL, response.getFirst().status());
        verify(taskRepository, times(1)).findAllByCreatedByAndStatus(parent, PENDING_APPROVAL);

    }

    @Test
    void get_TasksPendingApproval_valid_noPendingApproval() {
        // Arrange
        User parent = new User();
        parent.setId(1);
        CustomUserDetails userDetails = new CustomUserDetails(parent);

        when(userRepository.findById(1)).thenReturn(Optional.of(parent));
        when(taskRepository.findAllByCreatedByAndStatus(parent, PENDING_APPROVAL)).thenReturn(Collections.emptyList());

        // Act
        var response = taskService.getTasksPendingApproval(userDetails);

        // Assert
        assertTrue(response.isEmpty());
        verify(taskRepository, times(1)).findAllByCreatedByAndStatus(parent, PENDING_APPROVAL);

    }

}