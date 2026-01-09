package com.fredande.rewardsappbackend.service;

import com.fredande.rewardsappbackend.config.CustomUserDetails;
import com.fredande.rewardsappbackend.dto.LoginRequest;
import com.fredande.rewardsappbackend.dto.ParentRegistrationRequest;
import com.fredande.rewardsappbackend.model.User;
import com.fredande.rewardsappbackend.repository.UserRepository;
import jakarta.persistence.EntityExistsException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AuthenticationServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    @Mock
    UserDetailsService userDetailsService;

    @InjectMocks
    private AuthenticationServiceImpl authenticationService;

    /**
     * A user should be saved if the email and password is valid and the email is not registered.
     **/
    @Test
    void register_valid() {
        //Arrange
        String email = "test@test.test";
        String password = "pass1234";
        ParentRegistrationRequest parentRegistrationRequest = new ParentRegistrationRequest(email, password);
        when(userRepository.findByEmail(parentRegistrationRequest.getEmail())).thenReturn(Optional.empty());

        //Act
        authenticationService.registerParent(parentRegistrationRequest);

        //Assert
        verify(userRepository).findByEmail(any());
        verify(userRepository).save(any());
    }

    /**
     * A user should not be able to register with an email address that is already registered
     **/
    @Test
    void register_invalid_email_alreadyExists() {
        //Arrange
        String email = "test@test.test";
        String password = "pass1234";
        ParentRegistrationRequest parentRegistrationRequest = new ParentRegistrationRequest(email, password);
        User user = new User();
        user.setEmail(email);
        user.setPassword(password);
        when(userRepository.findByEmail(parentRegistrationRequest.getEmail())).thenReturn(Optional.of(user));

        //Act

        //Assert
        assertThrows(EntityExistsException.class, () -> authenticationService.registerParent(parentRegistrationRequest));
        verify(userRepository).findByEmail(parentRegistrationRequest.getEmail());
    }

    /**
     *
     */
    @Test
    void authenticate_valid() {
        // Arrange
        String email = "test@test.test";
        String password = "pass1234";
        User user = new User();
        user.setEmail("test@test.test");
        user.setPassword(password);
        CustomUserDetails customUserDetails = new CustomUserDetails(user);
        LoginRequest loginRequest = new LoginRequest(email, password);
        when(userDetailsService.loadUserByUsername(loginRequest.email())).thenReturn(customUserDetails);

        // Act
        UserDetails userDetails = userDetailsService.loadUserByUsername(loginRequest.email());

        // Assert
        assert (userDetails.getUsername().equals(email));
        assertEquals(userDetails, customUserDetails);
        verify(userDetailsService).loadUserByUsername(loginRequest.email());
    }

}

// TODO Register child unit tests