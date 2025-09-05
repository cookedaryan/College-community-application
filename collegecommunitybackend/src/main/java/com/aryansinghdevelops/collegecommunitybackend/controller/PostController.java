package com.aryansinghdevelops.collegecommunitybackend.controller;

import com.aryansinghdevelops.collegecommunitybackend.dto.PostDto;
import com.aryansinghdevelops.collegecommunitybackend.model.User;
import com.aryansinghdevelops.collegecommunitybackend.service.PostService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/posts")
@RequiredArgsConstructor
public class PostController {

    // Inject the PostService instead of the repository
    private final PostService postService;

    // This endpoint now uses the service to create the post
    @PostMapping
    public ResponseEntity<PostDto.PostResponse> createPost(
            @RequestBody PostDto.PostCreateRequest request,
            @AuthenticationPrincipal User currentUser) {

        PostDto.PostResponse createdPost = postService.createPost(request, currentUser);
        return new ResponseEntity<>(createdPost, HttpStatus.CREATED);
    }

    // New endpoint to get all posts
    @GetMapping
    public ResponseEntity<List<PostDto.PostResponse>> getAllPosts() {
        List<PostDto.PostResponse> posts = postService.getAllPosts();
        return ResponseEntity.ok(posts);
    }
}