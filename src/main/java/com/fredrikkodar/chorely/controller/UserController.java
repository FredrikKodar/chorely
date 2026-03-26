package com.fredrikkodar.chorely.controller;

import com.fredrikkodar.chorely.config.CustomUserDetails;
import com.fredrikkodar.chorely.dto.ParentUpdateRequest;
import com.fredrikkodar.chorely.dto.UserIdAndFirstNameResponse;
import com.fredrikkodar.chorely.dto.UserResponse;
import com.fredrikkodar.chorely.service.UserService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/users")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    // Read
    @GetMapping("/me")
    public ResponseEntity<UserResponse> getCurrentUser(@AuthenticationPrincipal CustomUserDetails userDetails) {
        return ResponseEntity.status(200).body(userService.getUserById(userDetails.getId(), userDetails));
    }

    @GetMapping("{id}")
    public ResponseEntity<UserResponse> getUserById(@PathVariable Integer id,
                                                    @AuthenticationPrincipal CustomUserDetails userDetails) {
        return ResponseEntity.status(200).body(userService.getUserById(id, userDetails));
    }

    @GetMapping("children")
    public ResponseEntity<List<UserIdAndFirstNameResponse>> getChildren(@AuthenticationPrincipal CustomUserDetails userDetails) {
        return ResponseEntity.status(200).body(userService.getChildren(userDetails));
    }

    @PatchMapping()
    public ResponseEntity<UserResponse> updateUser(@RequestBody @Valid ParentUpdateRequest request,
                                                   @AuthenticationPrincipal CustomUserDetails userDetails) {
        return ResponseEntity.status(200).body(userService.updateUser(request, userDetails));
    }

    @PatchMapping("children/{childId}")
    public ResponseEntity<UserIdAndFirstNameResponse> updateChild(@PathVariable Integer childId,
                                                                  @RequestBody UserIdAndFirstNameResponse updatedChild,
                                                                  @AuthenticationPrincipal CustomUserDetails userDetails) {
        return ResponseEntity.status(200).body(userService.updateChild(childId, userDetails, updatedChild));
    }

}
