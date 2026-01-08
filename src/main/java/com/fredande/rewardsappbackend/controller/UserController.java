package com.fredande.rewardsappbackend.controller;

import com.fredande.rewardsappbackend.CustomUserDetails;
import com.fredande.rewardsappbackend.dto.ChildRegistrationRequest;
import com.fredande.rewardsappbackend.dto.ChildResponse;
import com.fredande.rewardsappbackend.dto.UserResponse;
import com.fredande.rewardsappbackend.service.UserService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/users")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    // Create
    @PostMapping("/register-child")
    public ResponseEntity<ChildResponse> registerChild(@RequestBody @Valid ChildRegistrationRequest request,
                                                       @AuthenticationPrincipal CustomUserDetails userDetails) {
        return ResponseEntity.status(201).body(userService.registerChild(request, userDetails));
    }

    @GetMapping("{id}")
    public ResponseEntity<UserResponse> getUserById(@PathVariable Integer id,
                                                    @AuthenticationPrincipal CustomUserDetails userDetails) {
        return ResponseEntity.status(200).body(userService.getUserById(id, userDetails));
    }

}
