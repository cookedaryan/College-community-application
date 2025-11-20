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

    @GetMapping
    public ResponseEntity<List<PostDto.PostResponse>> getAllPosts(@AuthenticationPrincipal User currentUser) {
        // We pass currentUser so the service can check which posts they have already voted on
        return ResponseEntity.ok(postService.getAllPosts(currentUser));
    }

    // NEW: Endpoint for Upvoting/Downvoting
    @PostMapping("/{postId}/vote")
    public ResponseEntity<Void> votePost(
            @PathVariable Long postId,
            @RequestBody PostDto.VoteRequest request,
            @AuthenticationPrincipal User currentUser) {

        // Convert string request to Enum
        VoteType type = VoteType.valueOf(request.getVoteType());

        postService.vote(postId, type, currentUser);

        return ResponseEntity.ok().build();
    }
}