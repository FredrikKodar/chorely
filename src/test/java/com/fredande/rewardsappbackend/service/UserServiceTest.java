package com.fredande.rewardsappbackend.service;

import com.fredande.rewardsappbackend.config.CustomUserDetails;
import com.fredande.rewardsappbackend.model.Task;
import com.fredande.rewardsappbackend.model.User;
import com.fredande.rewardsappbackend.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static com.fredande.rewardsappbackend.enums.TaskStatus.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    UserRepository userRepository;

    @InjectMocks
    UserService userService;

    /**
     * If a task status has switched to APPROVED, the tasks points should be added to the users totalPoints and
     * currentPoints.
     */
    @Test
    void update_valid_points_increase() {
        // Arrange
        User user = new User();
        user.setId(1);
        user.setCurrentPoints(10);
        user.setTotalPoints(20);
        when(userRepository.findById(any(Integer.class))).thenReturn(Optional.of(user));

        // Act
        userService.updatePoints(1, 10, APPROVED);
        User updatedUser = userRepository.findById(user.getId()).orElse(null);

        // Assert
        assert updatedUser != null;
        assertEquals(30, updatedUser.getTotalPoints());
        assertEquals(20, updatedUser.getCurrentPoints());
        verify(userRepository, times(2)).findById(any(Integer.class));

    }

    /**
     * If a task status has switched from PENDING_APPROVAL to ASSIGNED, the tasks points should be retracted from the users totalPoints and
     * currentPoints.
     */
    @Test
    void update_valid_points_decrease() {
        // Arrange
        User user = new User();
        user.setId(1);
        user.setCurrentPoints(10);
        user.setTotalPoints(20);
        when(userRepository.findById(any(Integer.class))).thenReturn(Optional.of(user));

        // Act
        userService.updatePoints(1, 10, ASSIGNED);
        User updatedUser = userRepository.findById(user.getId()).orElse(null);

        // Assert
        assert updatedUser != null;
        assertEquals(10, updatedUser.getTotalPoints());
        assertEquals(0, updatedUser.getCurrentPoints());
        verify(userRepository, times(2)).findById(any(Integer.class));

    }

    @Test
    void get_valid() {
        // Arrange
        User user = new User();
        user.setId(1);
        List<Task> tasks = new ArrayList<>();
        Task task1 = new Task();
        Task task2 = new Task();
        task1.setUser(user);
        task1.setStatus(PENDING_APPROVAL);
        task2.setUser(user);
        task2.setStatus(APPROVED);
        tasks.add(task1);
        tasks.add(task2);
        user.setTasks(tasks);
        CustomUserDetails userDetails = new CustomUserDetails(user);
        when(userRepository.findById(user.getId())).thenReturn(Optional.of(user));

        // Act
        var response = userService.getUserById(user.getId(), userDetails);

        // Assert
        assertEquals(1, response.numTasksCompleted());
        assertEquals(1, response.numTasksOpen());
        assertEquals(2, response.numTasksTotal());
        verify(userRepository, times(1)).findById(any(Integer.class));

    }

    @Test
    void get_children_valid_oneChild() {
        // Arrange
        User parent = new User();
        parent.setId(1);
        CustomUserDetails userDetails = new CustomUserDetails(parent);
        User child = new User();
        List<User> children = new ArrayList<>();
        child.setId(2);
        child.setFirstName("Bob");
        child.setParent(parent);
        children.add(child);
        when(userRepository.findById(parent.getId())).thenReturn(Optional.of(parent));
        when(userRepository.findAllByParent(parent)).thenReturn(children);

        // Act
        var response = userService.getChildren(userDetails);

        //Assert
        assertEquals("Bob", response.getFirst().firstName());
        verify(userRepository, times(1)).findById(any(Integer.class));
        verify(userRepository, times(1)).findAllByParent(any(User.class));

    }

    @Test
    void get_children_valid_noChildren() {
        // Arrange
        User parent = new User();
        parent.setId(1);
        CustomUserDetails userDetails = new CustomUserDetails(parent);
        List<User> children = new ArrayList<>();
        when(userRepository.findById(parent.getId())).thenReturn(Optional.of(parent));
        when(userRepository.findAllByParent(parent)).thenReturn(children);

        // Act
        var response = userService.getChildren(userDetails);

        //Assert
        assertEquals(0, response.size());
        verify(userRepository, times(1)).findById(any(Integer.class));
        verify(userRepository, times(1)).findAllByParent(any(User.class));

    }

}