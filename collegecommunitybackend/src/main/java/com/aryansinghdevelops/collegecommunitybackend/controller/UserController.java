package com.aryansinghdevelops.collegecommunitybackend.controller;

import com.aryansinghdevelops.collegecommunitybackend.dto.PostDto;
import com.aryansinghdevelops.collegecommunitybackend.dto.ProfileResponseDto;
import com.aryansinghdevelops.collegecommunitybackend.dto.UpdateProfileRequest;
import com.aryansinghdevelops.collegecommunitybackend.dto.UserDto;
import com.aryansinghdevelops.collegecommunitybackend.model.User;
import com.aryansinghdevelops.collegecommunitybackend.service.PostService;
import com.aryansinghdevelops.collegecommunitybackend.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;
    private final PostService postService;

    // 1. Get Current User Info (Used for Navbar, checking Role, etc.)
    @GetMapping("/me")
    public ResponseEntity<UserDto> getCurrentUser(@AuthenticationPrincipal User currentUser) {
        return ResponseEntity.ok(new UserDto(
                currentUser.getDisplayName(),
                currentUser.getAvatarUrl(),
                currentUser.getBio(),
                currentUser.getSkills(),
                currentUser.getRole().name() // Sending role is crucial for Club Owner permissions
        ));
    }

    // 2. Update Profile (Bio, Gender, Skills, Avatar)
    @PutMapping("/me")
    public ResponseEntity<UserDto> updateProfile(
            @RequestBody UpdateProfileRequest request,
            @AuthenticationPrincipal User currentUser) {
        return ResponseEntity.ok(userService.updateProfile(currentUser, request));
    }

    // 3. Get Public Profile Stats (Follower counts, Bio, isFollowing check)
    @GetMapping("/{username}/profile")
    public ResponseEntity<ProfileResponseDto> getUserProfile(
            @PathVariable String username,
            @AuthenticationPrincipal User currentUser) {
        return ResponseEntity.ok(userService.getProfile(username, currentUser));
    }

    // 4. Get User's Posts (Paginated for the Profile Grid)
    @GetMapping("/{username}/posts")
    public ResponseEntity<List<PostDto.PostResponse>> getUserPosts(
            @PathVariable String username,
            @AuthenticationPrincipal User currentUser,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "12") int size) { // Default to 12 for a nice 3x4 grid

        User user = userService.findByUsername(username);
        return ResponseEntity.ok(postService.getPostsByUser(user, page, size, currentUser));
    }

    // 5. Follow a User
    @PostMapping("/{username}/follow")
    public ResponseEntity<?> followUser(
            @PathVariable String username,
            @AuthenticationPrincipal User currentUser) {
        userService.followUser(currentUser, username);
        return ResponseEntity.ok().build();
    }

    // 6. Unfollow a User
    @DeleteMapping("/{username}/follow")
    public ResponseEntity<?> unfollowUser(
            @PathVariable String username,
            @AuthenticationPrincipal User currentUser) {
        userService.unfollowUser(currentUser, username);
        return ResponseEntity.ok().build();
    }
}