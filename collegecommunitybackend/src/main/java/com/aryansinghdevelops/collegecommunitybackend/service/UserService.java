package com.aryansinghdevelops.collegecommunitybackend.service;

import com.aryansinghdevelops.collegecommunitybackend.dto.ProfileResponseDto;
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

    @Transactional(readOnly = true)
    public ProfileResponseDto getProfile(String profileUsername, User currentUser) {
        User profileUser = findByUsername(profileUsername);

        ProfileResponseDto response = new ProfileResponseDto();
        response.setUsername(profileUser.getDisplayName());
        response.setAvatarUrl(profileUser.getAvatarUrl());
        response.setPostCount(profileUser.getPosts().size());
        response.setFollowerCount(profileUser.getFollowers().size());
        response.setFollowingCount(profileUser.getFollowing().size());

        // FIXED: Now passing 'currentUser' as the second argument
        response.setPosts(postService.getPostsByUser(profileUser, currentUser));

        response.setFollowing(profileUser.getFollowers().contains(currentUser));

        return response;
    }
}