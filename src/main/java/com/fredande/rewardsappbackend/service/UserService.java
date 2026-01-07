package com.fredande.rewardsappbackend.service;

import com.fredande.rewardsappbackend.CustomUserDetails;
import com.fredande.rewardsappbackend.dto.ChildRegistrationRequest;
import com.fredande.rewardsappbackend.dto.ChildResponse;
import com.fredande.rewardsappbackend.dto.UserResponse;
import com.fredande.rewardsappbackend.enums.Role;
import com.fredande.rewardsappbackend.mapper.UserMapper;
import com.fredande.rewardsappbackend.model.User;
import com.fredande.rewardsappbackend.repository.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;

@Service
public class UserService {

    private final UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public void updatePoints(Integer userId, Integer points, boolean done) {
        User savedUser = userRepository.findById(userId).orElseThrow();
        if (done) {
            savedUser.setCurrentPoints(savedUser.getCurrentPoints() + points);
            savedUser.setTotalPoints(savedUser.getTotalPoints() + points);
        } else {
            savedUser.setCurrentPoints(savedUser.getCurrentPoints() - points);
            savedUser.setTotalPoints(savedUser.getTotalPoints() - points);
        }
    }

    public UserResponse getUserById(Integer id, CustomUserDetails userDetails) {
        User user = userRepository.findById(userDetails.getId()).orElseThrow(EntityNotFoundException::new);
        if (!id.equals(user.getId())) {
            throw new EntityNotFoundException();
        }
        return UserMapper.INSTANCE.userToUserResponse(user);
    }

    @PreAuthorize("hasRole('PARENT')")
    public ChildResponse registerChild(ChildRegistrationRequest request, CustomUserDetails userDetails) {
        User child = new User();
        User parent = userRepository.findById(userDetails.getId()).orElseThrow(EntityNotFoundException::new);
        child.setFirstName(request.getFirstName());
        child.setParent(parent);
        child.setRole(Role.CHILD);
        return UserMapper.INSTANCE.userToChildResponse(userRepository.save(child));
    }

}
