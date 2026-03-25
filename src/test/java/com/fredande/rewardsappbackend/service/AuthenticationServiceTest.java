package com.fredande.rewardsappbackend.service;

import com.fredande.rewardsappbackend.config.CustomUserDetails;
import com.fredande.rewardsappbackend.config.CustomUserDetailsService;
import com.fredande.rewardsappbackend.dto.ChildRegistrationRequest;
import com.fredande.rewardsappbackend.dto.LoginRequest;
import com.fredande.rewardsappbackend.dto.ParentRegistrationRequest;
import com.fredande.rewardsappbackend.model.User;
import com.fredande.rewardsappbackend.repository.UserRepository;
import jakarta.persistence.EntityExistsException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static com.fredande.rewardsappbackend.testUtils.TestUtils.FIRST_NAME;
import static com.fredande.rewardsappbackend.testUtils.TestUtils.LAST_NAME;
import static com.fredande.rewardsappbackend.enums.Role.CHILD;
import static com.fredande.rewardsappbackend.enums.Role.PARENT;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AuthenticationServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    AuthenticationManager authenticationManager;

    @Mock
    private PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    @Mock
    CustomUserDetailsService userDetailsService;

    @InjectMocks
    private AuthenticationServiceImpl authenticationService;

    /**
     * A child user should be saved if the username and passcode is valid and the username is not registered.
     **/
    @Test
    void registerChild_valid() {
        //Arrange
        String username = "testUsername";
        String passcode = "12345678";
        String firstName = "Tester";
        User parent = new User();
        CustomUserDetails userDetails = new CustomUserDetails(parent);
        ChildRegistrationRequest childRegistrationRequest = new ChildRegistrationRequest();
        childRegistrationRequest.setUsername(username);
        childRegistrationRequest.setPassword(passcode);
        childRegistrationRequest.setFirstName(firstName);
        ArgumentCaptor<User> userCaptor = ArgumentCaptor.forClass(User.class);
        when(userRepository.findByEmailOrUsername(childRegistrationRequest.getUsername(),
                childRegistrationRequest.getUsername())).thenReturn(Optional.empty());
        when(userRepository.findById(parent.getId())).thenReturn(Optional.of(parent));
        when(passwordEncoder.encode(passcode)).thenReturn("encoded_password");

        //Act
        authenticationService.registerChild(childRegistrationRequest, userDetails);

        //Assert
        verify(userRepository).save(userCaptor.capture());
        assertEquals("encoded_password", userCaptor.getValue().getPassword());
        assertEquals(username, userCaptor.getValue().getUsername());
        assertEquals(parent, userCaptor.getValue().getParent());
        assertEquals(CHILD, userCaptor.getValue().getRole());
        verify(userRepository).findById(any());
    }

    /**
     * A user should not be able to register a child with a username that is already registered
     *
     * @Throws EntityExistsException
     **/
    @Test
    void registerChild_invalid_username_alreadyExists() {
        //Arrange
        String username = "testUsername";
        String passcode = "12345678";
        String firstName = "Tester";
        User parent = new User();
        CustomUserDetails userDetails = new CustomUserDetails(parent);
        ChildRegistrationRequest childRegistrationRequest = new ChildRegistrationRequest();
        childRegistrationRequest.setUsername(username);
        childRegistrationRequest.setPassword(passcode);
        childRegistrationRequest.setFirstName(firstName);
        User existingUser = new User();
        existingUser.setUsername(username);
        when(userRepository.findByEmailOrUsername(username, username)).thenReturn(Optional.of(existingUser));

        //Act

        //Assert
        assertThrows(EntityExistsException.class,
                () -> authenticationService.registerChild(childRegistrationRequest, userDetails));
        verify(userRepository).findByEmailOrUsername(username, username);
    }

    /**
     * A user should be saved if the email and password is valid and the email is not registered.
     **/
    @Test
    void registerParent_valid() {
        //Arrange
        String email = "test@test.test";
        String password = "pass1234";
        ParentRegistrationRequest request = new ParentRegistrationRequest(email, password, FIRST_NAME, LAST_NAME);
        ArgumentCaptor<User> userCaptor = ArgumentCaptor.forClass(User.class);
        when(userRepository.findByEmail(request.email())).thenReturn(Optional.empty());
        when(passwordEncoder.encode(password)).thenReturn("encoded_password");

        //Act
        authenticationService.registerParent(request);

        //Assert
        verify(userRepository).save(userCaptor.capture());
        assertEquals("encoded_password", userCaptor.getValue().getPassword());
        assertEquals("test@test.test", userCaptor.getValue().getEmail());
        assertEquals(FIRST_NAME, userCaptor.getValue().getFirstName());
        assertEquals(LAST_NAME, userCaptor.getValue().getLastName());
        assertEquals(PARENT, userCaptor.getValue().getRole());
        verify(userRepository).findByEmail(any());
    }

    /**
     * A user should not be able to register with an email address that is already registered
     *
     * @Throws EntityExistsException
     **/
    @Test
    void registerParent_invalid_email_alreadyExists() {
        //Arrange
        String email = "test@test.test";
        String password = "pass1234";
        ParentRegistrationRequest request = new ParentRegistrationRequest(email, password, FIRST_NAME, LAST_NAME);
        User existingUser = new User();
        existingUser.setEmail(email);
        when(userRepository.findByEmail(email)).thenReturn(Optional.of(existingUser));

        //Act

        //Assert
        assertThrows(EntityExistsException.class,
                () -> authenticationService.registerParent(request));
        verify(userRepository).findByEmail(email);
    }

    /**
     * Should pass when using valid email and password
     */
    @Test
    void authenticate_valid() {
        // Arrange
        String email = "test@test.test";
        String password = "pass1234";
        User user = new User();
        user.setEmail(email);
        user.setPassword(password);
        CustomUserDetails customUserDetails = new CustomUserDetails(user);
        LoginRequest loginRequest = new LoginRequest(email, password);
        when(authenticationService.authenticate(loginRequest.email(), loginRequest.password())).thenReturn(customUserDetails);

        // Act
        UserDetails userDetails = authenticationService.authenticate(loginRequest.email(), loginRequest.password());

        // Assert
        assert (userDetails.getUsername().equals(email));
        assertEquals(userDetails, customUserDetails);
        verify(userDetailsService).loadUserByUsername(loginRequest.email());
    }

}