package com.aryansinghdevelops.collegecommunitybackend.service;

import com.aryansinghdevelops.collegecommunitybackend.dto.ProfileResponseDto;
import com.aryansinghdevelops.collegecommunitybackend.dto.UpdateProfileRequest;
import com.aryansinghdevelops.collegecommunitybackend.dto.UserDto;
import com.aryansinghdevelops.collegecommunitybackend.model.User;
import com.aryansinghdevelops.collegecommunitybackend.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final PostService postService;

    @Transactional(readOnly = true)
    public User findByUsername(String username) {
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found: " + username));
    }

    @Transactional
    public void followUser(User currentUser, String usernameToFollow) {
        User userToFollow = findByUsername(usernameToFollow);
        currentUser.getFollowing().add(userToFollow);
        userRepository.save(currentUser);
    }

    @Transactional
    public void unfollowUser(User currentUser, String usernameToUnfollow) {
        User userToUnfollow = findByUsername(usernameToUnfollow);
        currentUser.getFollowing().remove(userToUnfollow);
        userRepository.save(currentUser);
    }

    @Transactional
    public UserDto updateProfile(User currentUser, UpdateProfileRequest request) {
        if (request.getUsername() != null) currentUser.setUsername(request.getUsername());
        if (request.getBio() != null) currentUser.setBio(request.getBio());
        if (request.getGender() != null) currentUser.setGender(request.getGender());
        if (request.getDateOfBirth() != null) currentUser.setDateOfBirth(request.getDateOfBirth());
        if (request.getSkills() != null) currentUser.setSkills(request.getSkills());
        if (request.getAvatarUrl() != null) currentUser.setAvatarUrl(request.getAvatarUrl());

        User savedUser = userRepository.save(currentUser);

        // UPDATED CONSTRUCTOR CALL to include role
        return new UserDto(
                savedUser.getDisplayName(),
                savedUser.getAvatarUrl(),
                savedUser.getBio(),
                savedUser.getSkills(),
                savedUser.getRole().name()
        );
    }

    // In UserService.java

    @Transactional(readOnly = true)
    public ProfileResponseDto getProfile(String profileUsername, User currentUser) {
        User profileUser = findByUsername(profileUsername);

        ProfileResponseDto response = new ProfileResponseDto();
        response.setUsername(profileUser.getDisplayName());
        response.setAvatarUrl(profileUser.getAvatarUrl());

        response.setBio(profileUser.getBio());
        response.setGender(profileUser.getGender());
        response.setDateOfBirth(profileUser.getDateOfBirth());
        response.setSkills(profileUser.getSkills());

        response.setPostCount(profileUser.getPosts().size());
        response.setFollowerCount(profileUser.getFollowers().size());
        response.setFollowingCount(profileUser.getFollowing().size());

        // REMOVED: response.setPosts(...); -> We fetch this separately now for speed!
        response.setPosts(java.util.Collections.emptyList());

        response.setFollowing(profileUser.getFollowers().contains(currentUser));

        return response;
    }
}