package com.aryansinghdevelops.collegecommunitybackend.controller;

import com.aryansinghdevelops.collegecommunitybackend.dto.ProfileResponseDto;
import com.aryansinghdevelops.collegecommunitybackend.dto.UserDto;
import com.aryansinghdevelops.collegecommunitybackend.model.User;
import com.aryansinghdevelops.collegecommunitybackend.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @GetMapping("/me")
    public ResponseEntity<UserDto> getCurrentUser(@AuthenticationPrincipal User currentUser) {
        return ResponseEntity.ok(new UserDto(currentUser.getDisplayName()));
    }

    @GetMapping("/{username}/profile")
    public ResponseEntity<ProfileResponseDto> getUserProfile(
            @PathVariable String username,
            @AuthenticationPrincipal User currentUser) {
        return ResponseEntity.ok(userService.getProfile(username, currentUser));
    }

    @PostMapping("/{username}/follow")
    public ResponseEntity<?> followUser(
            @PathVariable String username,
            @AuthenticationPrincipal User currentUser) {
        userService.followUser(currentUser, username);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{username}/follow")
    public ResponseEntity<?> unfollowUser(
            @PathVariable String username,
            @AuthenticationPrincipal User currentUser) {
        userService.unfollowUser(currentUser, username);
        return ResponseEntity.ok().build();
    }
}