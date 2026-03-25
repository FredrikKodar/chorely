package com.fredande.rewardsappbackend.service;

import com.fredande.rewardsappbackend.config.CustomUserDetails;
import com.fredande.rewardsappbackend.dto.ParentUpdateRequest;
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
    public UserIdAndFirstNameResponse updateChild(Integer childId, CustomUserDetails userDetails, UserIdAndFirstNameResponse updatedChild) {
        User parent = userRepository.findById(userDetails.getId()).orElseThrow(EntityNotFoundException::new);
        User child = userRepository.findById(childId).orElseThrow(EntityNotFoundException::new);

        // Verify that the child belongs to this parent
        if (!child.getParent().getId().equals(parent.getId())) {
            throw new EntityNotFoundException();
        }

        // Update the child's information
        child.setFirstName(updatedChild.firstName());
        User savedChild = userRepository.save(child);

        return UserMapper.INSTANCE.userToUserIdAndFirstNameResponse(savedChild);
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
        User requestingUser = userRepository.findById(userDetails.getId()).orElseThrow(EntityNotFoundException::new);
        User targetUser = userRepository.findById(id).orElseThrow(EntityNotFoundException::new);

        // Allow users to access their own data
        if (id.equals(requestingUser.getId())) {
            return UserMapper.INSTANCE.userToUserResponse(requestingUser);
        }

        // Allow parents to access their children's data
        if (requestingUser.getRole().name().equals("ROLE_PARENT") &&
                targetUser.getParent() != null &&
                targetUser.getParent().getId().equals(requestingUser.getId())) {
            return UserMapper.INSTANCE.userToUserResponse(targetUser);
        }

        // Otherwise, throw exception
        throw new EntityNotFoundException();
    }

    @PreAuthorize("hasRole('PARENT')")
    public UserResponse updateUser(ParentUpdateRequest request, CustomUserDetails userDetails) {
        User savedUser = userRepository.findById(userDetails.getId()).orElseThrow();
        savedUser.setFirstName(request.firstName());
        savedUser.setLastName(request.lastName());
        return UserMapper.INSTANCE.userToUserResponse(userRepository.save(savedUser));
    }

}
