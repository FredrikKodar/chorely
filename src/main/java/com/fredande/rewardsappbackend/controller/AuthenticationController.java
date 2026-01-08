package com.fredande.rewardsappbackend.controller;

import com.fredande.rewardsappbackend.CustomUserDetails;
import com.fredande.rewardsappbackend.dto.AuthResponse;
import com.fredande.rewardsappbackend.dto.ChildRegistrationRequest;
import com.fredande.rewardsappbackend.dto.LoginRequest;
import com.fredande.rewardsappbackend.dto.ParentRegistrationRequest;
import com.fredande.rewardsappbackend.service.AuthenticationServiceDef;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
public class AuthenticationController {

    private final Long EXPIRES_IN = 86400L;

    private final AuthenticationServiceDef authService;

    public AuthenticationController(AuthenticationServiceDef authService) {
        this.authService = authService;
    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@RequestBody LoginRequest loginRequest) {
        UserDetails userDetails = authService.authenticate(
                loginRequest.email(),
                loginRequest.password());
        String token = authService.generateToken(userDetails);
        String roles = userDetails.getAuthorities().toString();
        AuthResponse authResponse = new AuthResponse(token, EXPIRES_IN, roles);
        return ResponseEntity.ok(authResponse);
    }

    @PostMapping("/register")
    public ResponseEntity<String> registerParent(@RequestBody @Valid ParentRegistrationRequest parentRegistrationRequest) {
        authService.registerParent(parentRegistrationRequest);
        return ResponseEntity.status(HttpStatusCode.valueOf(201)).body("User registered successfully");
    }

    @PostMapping("/register-child")
    public ResponseEntity<String> registerChild(@RequestBody @Valid ChildRegistrationRequest childRegistrationRequest,
                                                @AuthenticationPrincipal CustomUserDetails customUserDetails) {
        authService.registerChild(childRegistrationRequest, customUserDetails);
        return ResponseEntity.status(HttpStatusCode.valueOf(201)).body("User registered successfully");
    }

}
