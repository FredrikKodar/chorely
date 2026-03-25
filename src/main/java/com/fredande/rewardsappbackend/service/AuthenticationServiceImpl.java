package com.fredande.rewardsappbackend.service;

import com.fredande.rewardsappbackend.config.CustomUserDetails;
import com.fredande.rewardsappbackend.config.CustomUserDetailsService;
import com.fredande.rewardsappbackend.dto.ChildRegistrationRequest;
import com.fredande.rewardsappbackend.dto.ParentRegistrationRequest;
import com.fredande.rewardsappbackend.enums.Role;
import com.fredande.rewardsappbackend.model.User;
import com.fredande.rewardsappbackend.repository.UserRepository;
import io.jsonwebtoken.Jwts;
import jakarta.persistence.EntityExistsException;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Service
public class AuthenticationServiceImpl implements AuthenticationServiceDef {

    private final AuthenticationManager authenticationManager;
    private final CustomUserDetailsService userDetailsService;
    private final JWTService jwtService;
    @Value("${jwt.expiration.ms:900000}")
    private Long expirationTime;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public AuthenticationServiceImpl(AuthenticationManager authenticationManager,
                                     CustomUserDetailsService userDetailsService,
                                     JWTService jwtService,
                                     UserRepository userRepository,
                                     PasswordEncoder passwordEncoder) {
        this.authenticationManager = authenticationManager;
        this.userDetailsService = userDetailsService;
        this.jwtService = jwtService;
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public UserDetails authenticate(String username, String password) {
        try {
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(username, password)
            );
        } catch (AuthenticationException e) {
            throw new BadCredentialsException("Invalid email or password", e);
        }
        return userDetailsService.loadUserByUsername(username);
    }

    @Override
    public String generateToken(UserDetails userDetails) {
        Map<String, Object> claims = new HashMap<>();
        return Jwts.builder()
                .claims(claims)
                .subject(userDetails.getUsername())
                .issuedAt(new Date(System.currentTimeMillis()))
                .expiration(new Date(System.currentTimeMillis() + expirationTime))
                .signWith(jwtService.getSigningKey())
                .compact();
    }

    @Override
    public void registerParent(ParentRegistrationRequest request) {
        if (userRepository.findByEmail(request.email()).isPresent()) {
            throw new EntityExistsException("Email already registered");
        }
        User user = new User();
        user.setPassword(passwordEncoder.encode(request.password()));
        user.setEmail(request.email());
        user.setRole(Role.PARENT);
        user.setFirstName(request.firstName());
        user.setLastName(request.lastName());
        userRepository.save(user);
    }

    @Override
    public void registerChild(ChildRegistrationRequest request,
                              CustomUserDetails userDetails) {
        if (userRepository.findByEmailOrUsername(
                        request.getUsername(),
                        request.getUsername())
                .isPresent()) {
            throw new EntityExistsException("Username already registered");
        }
        User child = new User();
        User parent = userRepository.findById(userDetails.getId()).orElseThrow(EntityNotFoundException::new);
        child.setPassword(passwordEncoder.encode(request.getPassword()));
        child.setUsername(request.getUsername());
        child.setFirstName(request.getFirstName());
        child.setParent(parent);
        child.setRole(Role.CHILD);
        userRepository.save(child);
    }

}
