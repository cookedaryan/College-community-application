package com.aryansinghdevelops.collegecommunitybackend.controller;

import com.aryansinghdevelops.collegecommunitybackend.dto.PostDto;
import com.aryansinghdevelops.collegecommunitybackend.model.User;
import com.aryansinghdevelops.collegecommunitybackend.model.VoteType;
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

    private final PostService postService;

    @PostMapping
    public ResponseEntity<PostDto.PostResponse> createPost(
            @RequestBody PostDto.PostCreateRequest request,
            @AuthenticationPrincipal User currentUser) {
        PostDto.PostResponse createdPost = postService.createPost(request, currentUser);
        return new ResponseEntity<>(createdPost, HttpStatus.CREATED);
    }

    // --- UPDATED ENDPOINT ---
    @GetMapping
    public ResponseEntity<List<PostDto.PostResponse>> getAllPosts(
            @AuthenticationPrincipal User currentUser,
            @RequestParam(defaultValue = "0") int page, // Start at page 0
            @RequestParam(defaultValue = "10") int size // Load 10 at a time
    ) {
        return ResponseEntity.ok(postService.getAllPosts(page, size, currentUser));
    }

    @PostMapping("/{postId}/vote")
    public ResponseEntity<Void> votePost(
            @PathVariable Long postId,
            @RequestBody PostDto.VoteRequest request,
            @AuthenticationPrincipal User currentUser) {
        VoteType type = VoteType.valueOf(request.getVoteType());
        postService.vote(postId, type, currentUser);
        return ResponseEntity.ok().build();
    }
}