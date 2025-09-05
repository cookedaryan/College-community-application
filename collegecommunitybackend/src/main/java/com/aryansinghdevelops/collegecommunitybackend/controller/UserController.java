package com.aryansinghdevelops.collegecommunitybackend.controller;

import com.aryansinghdevelops.collegecommunitybackend.dto.PostDto;
import com.aryansinghdevelops.collegecommunitybackend.dto.UserDto; // <-- ADD IMPORT
import com.aryansinghdevelops.collegecommunitybackend.model.User;
import com.aryansinghdevelops.collegecommunitybackend.service.PostService;
import com.aryansinghdevelops.collegecommunitybackend.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal; // <-- ADD IMPORT
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;
    private final PostService postService;

    // --- NEW METHOD ---
    @GetMapping("/me")
    public ResponseEntity<UserDto> getCurrentUser(@AuthenticationPrincipal User currentUser) {
        // If the code reaches here, the token is valid because of Spring Security.
        // @AuthenticationPrincipal automatically gives us the logged-in user.
        return ResponseEntity.ok(new UserDto(currentUser.getDisplayName()));
    }

    @GetMapping("/{username}/profile")
    public ResponseEntity<List<PostDto.PostResponse>> getUserProfile(@PathVariable String username) {
        User user = userService.findByUsername(username);
        List<PostDto.PostResponse> posts = postService.getPostsByUser(user);
        return ResponseEntity.ok(posts);
    }
}