package com.arena.user.service;

import com.arena.common.dto.PagedResponse;
import com.arena.common.dto.UserDTO;
import com.arena.common.exception.ConflictException;
import com.arena.common.exception.ResourceNotFoundException;
import com.arena.user.dto.UpdateProfileRequest;
import com.arena.user.dto.UserProfileDTO;
import com.arena.user.entity.User;
import com.arena.user.entity.UserStatistics;
import com.arena.user.mapper.UserMapper;
import com.arena.user.repository.UserRepository;
import com.arena.user.repository.UserStatisticsRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final UserStatisticsRepository statisticsRepository;
    private final UserMapper userMapper;

    @Transactional
    public UserDTO syncUser(String keycloakId, String username, String email) {
        log.info("Syncing user from Keycloak: keycloakId={}, username={}", keycloakId, username);

        return userRepository.findByKeycloakId(keycloakId)
                .map(existingUser -> {
                    // Update existing user if needed
                    if (!existingUser.getEmail().equals(email)) {
                        existingUser.setEmail(email);
                        userRepository.save(existingUser);
                    }
                    return userMapper.toDTO(existingUser);
                })
                .orElseGet(() -> {
                    // Create new user
                    User newUser = User.builder()
                            .keycloakId(keycloakId)
                            .username(username)
                            .email(email)
                            .build();

                    User savedUser = userRepository.save(newUser);

                    // Create statistics for new user
                    UserStatistics statistics = UserStatistics.builder()
                            .user(savedUser)
                            .build();
                    statisticsRepository.save(statistics);

                    log.info("Created new user: id={}, username={}", savedUser.getId(), savedUser.getUsername());

                    return userMapper.toDTO(savedUser);
                });
    }

    @Transactional(readOnly = true)
    public UserDTO getUserById(UUID id) {
        User user = findUserById(id);
        return userMapper.toDTO(user);
    }

    @Transactional(readOnly = true)
    public UserDTO getUserByKeycloakId(String keycloakId) {
        User user = userRepository.findByKeycloakId(keycloakId)
                .orElseThrow(() -> new ResourceNotFoundException("User", keycloakId));
        return userMapper.toDTO(user);
    }


    @Transactional(readOnly = true)
    public UserProfileDTO getUserProfile(UUID id) {
        User user = findUserById(id);
        return userMapper.toProfileDTO(user);
    }


    @Transactional(readOnly = true)
    public UserProfileDTO getUserProfileByUsername(String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("User", username));
        return userMapper.toProfileDTO(user);
    }


    @Transactional
    public UserProfileDTO updateProfile(UUID id, UpdateProfileRequest request) {
        User user = findUserById(id);

        // Check if username is being changed and if it's already taken
        if (request.getUsername() != null && !request.getUsername().equals(user.getUsername())) {
            if (userRepository.existsByUsername(request.getUsername())) {
                throw new ConflictException("Username already taken");
            }
            user.setUsername(request.getUsername());
        }

        if (request.getAvatarUrl() != null) {
            user.setAvatarUrl(request.getAvatarUrl());
        }

        if (request.getPreferredLanguage() != null && user.getStatistics() != null) {
            user.getStatistics().setPreferredLanguage(request.getPreferredLanguage());
        }

        User savedUser = userRepository.save(user);
        log.info("Updated profile for user: id={}", id);

        return userMapper.toProfileDTO(savedUser);
    }


    @Transactional(readOnly = true)
    public PagedResponse<UserDTO> searchUsers(String query, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<User> usersPage = userRepository.searchByUsername(query, pageable);

        List<UserDTO> users = userMapper.toDTOList(usersPage.getContent());

        return PagedResponse.of(
                users,
                usersPage.getNumber(),
                usersPage.getSize(),
                usersPage.getTotalElements(),
                usersPage.getTotalPages()
        );
    }

    @Transactional(readOnly = true)
    public boolean existsByKeycloakId(String keycloakId) {
        return userRepository.existsByKeycloakId(keycloakId);
    }

    // Helper method
    private User findUserById(UUID id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User", id.toString()));
    }
}