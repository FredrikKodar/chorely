package com.fredande.rewardsappbackend.service;

import com.fredande.rewardsappbackend.CustomUserDetails;
import com.fredande.rewardsappbackend.dto.UserIdAndFirstNameResponse;
import com.fredande.rewardsappbackend.dto.UserResponse;
import com.fredande.rewardsappbackend.enums.TaskStatus;
import com.fredande.rewardsappbackend.mapper.UserMapper;
import com.fredande.rewardsappbackend.model.User;
import com.fredande.rewardsappbackend.repository.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserService {

    private final UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @PreAuthorize("hasRole('PARENT')")
    public List<UserIdAndFirstNameResponse> getChildren(CustomUserDetails userDetails) {
        User user = userRepository.findById(userDetails.getId()).orElseThrow(EntityNotFoundException::new);
        return userRepository.findAllByParent(user)
                .stream()
                .map(
                        UserMapper.INSTANCE::userToUserIdAndFirstNameResponse
                )
                .toList();
    }

    @PreAuthorize("hasRole('PARENT')")
    public void updatePoints(Integer userId, Integer points, TaskStatus status) {
        User savedUser = userRepository.findById(userId).orElseThrow();
        if (status.equals(TaskStatus.APPROVED)) {
            savedUser.setCurrentPoints(savedUser.getCurrentPoints() + points);
            savedUser.setTotalPoints(savedUser.getTotalPoints() + points);
        } else {
            savedUser.setCurrentPoints(savedUser.getCurrentPoints() - points);
            savedUser.setTotalPoints(savedUser.getTotalPoints() - points);
        }
        userRepository.save(savedUser);
    }

    @PreAuthorize("hasRole('PARENT') or hasRole('CHILD')")
    public UserResponse getUserById(Integer id, CustomUserDetails userDetails) {
        User user = userRepository.findById(userDetails.getId()).orElseThrow(EntityNotFoundException::new);
        if (!id.equals(user.getId())) {
            throw new EntityNotFoundException();
        }
        return UserMapper.INSTANCE.userToUserResponse(user);
    }

}
