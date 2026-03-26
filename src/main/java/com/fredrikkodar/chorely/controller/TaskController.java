package com.fredrikkodar.chorely.controller;

import com.fredrikkodar.chorely.config.CustomUserDetails;
import com.fredrikkodar.chorely.dto.TaskCreationRequest;
import com.fredrikkodar.chorely.dto.TaskReadResponse;
import com.fredrikkodar.chorely.dto.TaskSavedResponse;
import com.fredrikkodar.chorely.dto.TaskUpdateRequest;
import com.fredrikkodar.chorely.service.TaskService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/tasks")
public class TaskController {

    private final TaskService taskService;

    public TaskController(TaskService taskService) {
        this.taskService = taskService;
    }


    // CREATE

    @PostMapping
    public ResponseEntity<TaskSavedResponse> create(
            @RequestBody @Valid TaskCreationRequest taskCreationRequest,
            @AuthenticationPrincipal CustomUserDetails userDetails) {
        return ResponseEntity.status(201).body(taskService.createTaskOnParent(taskCreationRequest, userDetails));
    }


    @PostMapping("/{childId}")
    public ResponseEntity<TaskSavedResponse> createTaskByChildId(
            @RequestBody @Valid TaskCreationRequest taskCreationRequest,
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @PathVariable Integer childId) {
        return ResponseEntity.status(201).body(taskService.createTaskOnChildByChildId(taskCreationRequest, userDetails, childId));
    }


    // READ

    @GetMapping
    public ResponseEntity<List<TaskReadResponse>> getAllTasksByUser(
            @AuthenticationPrincipal CustomUserDetails userDetails) {
        return ResponseEntity.status(200).body(taskService.getAllTasksByUser(userDetails));
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<List<TaskReadResponse>> getAllTasksByUserAndUserId(
            @PathVariable Integer userId,
            @AuthenticationPrincipal CustomUserDetails userDetails) {
        return ResponseEntity.status(200).body(taskService.getAllTasksByUserAndUserId(userId, userDetails));
    }

    @GetMapping("/{id}")
    public ResponseEntity<TaskReadResponse> getTaskByIdAndUser(
            @PathVariable Integer id,
            @AuthenticationPrincipal CustomUserDetails userDetails) {
        return ResponseEntity.status(200).body(taskService.getTaskByIdAndUser(id, userDetails));
    }

    @GetMapping("/pending-approval")
    public ResponseEntity<List<TaskReadResponse>> getTasksPendingApproval(
            @AuthenticationPrincipal CustomUserDetails userDetails) {
        return ResponseEntity.status(200).body(taskService.getTasksPendingApproval(userDetails));
    }


    // UPDATE

    @PatchMapping("/{id}")
    public ResponseEntity<TaskReadResponse> update(
            @PathVariable Integer id,
            @RequestBody @Valid TaskUpdateRequest updatedTask,
            @AuthenticationPrincipal CustomUserDetails userDetails) {
        return ResponseEntity.status(201).body(taskService.update(id, userDetails, updatedTask));
    }

    // Toggle status between ASSIGNED and PENDING_APPROVAL.
    @PatchMapping("/{id}/toggle-status-child")
    public ResponseEntity<TaskReadResponse> toggleStatusChild(
            @PathVariable Integer id,
            @AuthenticationPrincipal CustomUserDetails userDetails) {
        return ResponseEntity.status(201).body(taskService.toggleStatus(id, userDetails));
    }

    // Toggle status from PENDING_APPROVAL to APPROVED.
    @PatchMapping("/{id}/approve")
    public ResponseEntity<TaskReadResponse> approve(
            @PathVariable Integer id,
            @AuthenticationPrincipal CustomUserDetails userDetails) {
        return ResponseEntity.status(201).body(taskService.approve(id, userDetails));
    }

}
