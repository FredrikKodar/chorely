package com.fredande.rewardsappbackend.service;

import com.fredande.rewardsappbackend.config.CustomUserDetails;
import com.fredande.rewardsappbackend.dto.TaskCreationRequest;
import com.fredande.rewardsappbackend.dto.TaskReadResponse;
import com.fredande.rewardsappbackend.dto.TaskSavedResponse;
import com.fredande.rewardsappbackend.dto.TaskUpdateRequest;
import com.fredande.rewardsappbackend.mapper.TaskMapper;
import com.fredande.rewardsappbackend.model.Task;
import com.fredande.rewardsappbackend.model.User;
import com.fredande.rewardsappbackend.repository.TaskRepository;
import com.fredande.rewardsappbackend.repository.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

import static com.fredande.rewardsappbackend.enums.TaskStatus.*;

@Service
public class TaskService {

    private final TaskRepository taskRepository;
    private final UserRepository userRepository;
    private final UserService userService;

    public TaskService(TaskRepository taskRepository, UserRepository userRepository, UserService userService) {
        this.taskRepository = taskRepository;
        this.userRepository = userRepository;
        this.userService = userService;
    }

    @PreAuthorize("hasRole('PARENT')")
    public TaskSavedResponse createTaskOnParent(TaskCreationRequest taskCreationRequest, CustomUserDetails userDetails) {
        User user = new User();
        user.setId(userDetails.getId());
        Task task = new Task();
        task.setTitle(taskCreationRequest.title());
        task.setDescription(taskCreationRequest.description());
        task.setPoints(taskCreationRequest.points());
        task.setUser(user);
        taskRepository.save(task);
        return TaskMapper.INSTANCE.taskToTaskSavedResponse(task);
    }

    @PreAuthorize("hasRole('PARENT')")
    public TaskSavedResponse createTaskOnChildByChildId(TaskCreationRequest taskCreationRequest,
                                                        CustomUserDetails userDetails,
                                                        Integer childId) {
        User child = userRepository.findById(childId).orElseThrow(EntityNotFoundException::new);
        User parent = userRepository.findById(userDetails.getId()).orElseThrow(EntityNotFoundException::new);
        if (!child.getParent().equals(parent)) {
            throw new EntityNotFoundException();
        }
        Task task = new Task();
        task.setTitle(taskCreationRequest.title());
        task.setDescription(taskCreationRequest.description());
        task.setPoints(taskCreationRequest.points());
        task.setUser(child);
        task.setCreatedBy(parent);
        taskRepository.save(task);
        return TaskMapper.INSTANCE.taskToTaskSavedResponse(task);
    }

    @PreAuthorize("hasRole('PARENT') or hasRole('CHILD')")
    public TaskReadResponse getTaskByIdAndUser(Integer id, CustomUserDetails userDetails) {
        User user = userRepository.findById(userDetails.getId()).orElseThrow();
        Task savedTask = taskRepository.findByIdAndUser(id, user).orElse(null);
        if (savedTask == null) {
            throw new EntityNotFoundException("User-task mismatch");
        }
        return TaskMapper.INSTANCE.taskToTaskReadResponse(savedTask);
    }

    @PreAuthorize("hasRole('PARENT') or hasRole('CHILD')")
    public List<TaskReadResponse> getAllTasksByUser(CustomUserDetails userDetails) {
        return taskRepository.findByUser(userRepository.findById(userDetails.getId()).orElseThrow())
                .stream()
                .map(TaskMapper.INSTANCE::taskToTaskReadResponse)
                .toList();
    }

    @PreAuthorize("hasRole('PARENT')")
    public TaskReadResponse update(Integer id, CustomUserDetails userDetails, TaskUpdateRequest updatedTask) {
        Task savedTask = taskRepository.findById(id).orElseThrow(EntityNotFoundException::new);
        User user = userRepository.findById(userDetails.getId()).orElseThrow(EntityNotFoundException::new);
        boolean updated = false;
        if (!savedTask.getUser().equals(user)) {
            throw new EntityNotFoundException("Task-user mismatch");
        }
        if (updatedTask.title() != null && !savedTask.getTitle().equals(updatedTask.title())) {
            savedTask.setTitle(updatedTask.title());
            updated = true;
        }
        if (updatedTask.description() != null && !savedTask.getDescription().equals(updatedTask.description())) {
            updated = true;
            savedTask.setDescription(updatedTask.description());
        }
        if (updatedTask.points() != null && !savedTask.getPoints().equals(updatedTask.points())) {
            updated = true;
            savedTask.setPoints(updatedTask.points());
        }
        if (updatedTask.status() != null &&
                savedTask.getStatus() != updatedTask.status()) {
            updated = true;
            savedTask.setStatus(updatedTask.status());
            if (savedTask.getStatus().equals(APPROVED)
                    && (updatedTask.status().equals(PENDING_APPROVAL) || updatedTask.status().equals(ASSIGNED))
                    ||
                    (savedTask.getStatus().equals(PENDING_APPROVAL) || savedTask.getStatus().equals(ASSIGNED)
                            && updatedTask.status().equals(APPROVED))
            ) {
                userService.updatePoints(savedTask.getUser().getId(), savedTask.getPoints(), updatedTask.status());
            }
        }
        if (updated) {
            savedTask.setUpdated(new Date());
        }
        taskRepository.save(savedTask);
        return TaskMapper.INSTANCE.taskToTaskReadResponse(savedTask);
    }

    @PreAuthorize("hasRole('CHILD')")
    public TaskReadResponse toggleStatus(Integer id, CustomUserDetails userDetails) {
        Task savedTask = taskRepository.findById(id).orElseThrow(EntityNotFoundException::new);
        User user = userRepository.findById(userDetails.getId()).orElseThrow(EntityNotFoundException::new);
        if (!savedTask.getUser().equals(user)) {
            throw new EntityNotFoundException("Task-user mismatch");
        }
        if (savedTask.getStatus().equals(ASSIGNED)) {
            savedTask.setStatus(PENDING_APPROVAL);
        } else if (savedTask.getStatus().equals(PENDING_APPROVAL)) {
            savedTask.setStatus(ASSIGNED);
        } else {
            throw new EntityNotFoundException("User not allowed to change status");
        }
        savedTask.setUpdated(new Date());
        taskRepository.save(savedTask);
        return TaskMapper.INSTANCE.taskToTaskReadResponse(savedTask);
    }

    @PreAuthorize("hasRole('PARENT')")
    public TaskReadResponse approve(Integer id, CustomUserDetails userDetails) {
        User createdBy = userRepository.findById(userDetails.getId())
                .orElseThrow(() -> new EntityNotFoundException("User not found"));
        Task savedTask = taskRepository.findByIdAndCreatedBy(id, createdBy)
                .orElseThrow(() -> new EntityNotFoundException("User-task mismatch"));
        savedTask.setStatus(APPROVED);
        savedTask.setUpdated(new Date());
        userService.updatePoints(savedTask.getUser().getId(), savedTask.getPoints(), APPROVED);
        taskRepository.save(savedTask);
        return TaskMapper.INSTANCE.taskToTaskReadResponse(savedTask);
    }

}
