package com.aryansinghdevelops.collegecommunitybackend.service;

import com.aryansinghdevelops.collegecommunitybackend.dto.PostDto;
import com.aryansinghdevelops.collegecommunitybackend.model.Post;
import com.aryansinghdevelops.collegecommunitybackend.model.PostVote;
import com.aryansinghdevelops.collegecommunitybackend.model.User;
import com.aryansinghdevelops.collegecommunitybackend.model.VoteType;
import com.aryansinghdevelops.collegecommunitybackend.repository.PostRepository;
import com.aryansinghdevelops.collegecommunitybackend.repository.PostVoteRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PostService {

    private final PostRepository postRepository;
    private final PostVoteRepository postVoteRepository;

    /**
     * Creates a new post and associates it with the current user.
     * THIS IS THE METHOD YOUR CONTROLLER IS LOOKING FOR.
     */
    @Transactional
    public PostDto.PostResponse createPost(PostDto.PostCreateRequest request, User currentUser) {
        Post newPost = new Post();
        newPost.setContent(request.getContent());
        newPost.setImageUrl(request.getImageUrl());
        newPost.setUser(currentUser);

        Post savedPost = postRepository.save(newPost);

        // Pass currentUser to mapToPostResponse so we can handle the vote state (0 for new posts)
        return mapToPostResponse(savedPost, currentUser);
    }

    // Inside PostService.java

    @Transactional
    public void vote(Long postId, VoteType voteType, User currentUser) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new RuntimeException("Post not found"));

        Optional<PostVote> existingVote = postVoteRepository.findTopByPostAndUser(post, currentUser);

        if (existingVote.isPresent()) {
            PostVote vote = existingVote.get();
            if (vote.getVoteType().equals(voteType)) {
                // Toggle off
                postVoteRepository.delete(vote);
            } else {
                // Swap vote
                vote.setVoteType(voteType);
                postVoteRepository.save(vote);
            }
        } else {
            // New vote
            PostVote newVote = PostVote.builder()
                    .post(post)
                    .user(currentUser)
                    .voteType(voteType)
                    .build();
            postVoteRepository.save(newVote);
        }

        // --- THE FIX: Recalculate score from scratch ---
        // This ensures the score is ALWAYS correct, even if clicks happen fast
        int upvotes = postVoteRepository.countByPostAndVoteType(post, VoteType.UPVOTE);
        int downvotes = postVoteRepository.countByPostAndVoteType(post, VoteType.DOWNVOTE);

        post.setScore(upvotes - downvotes);
        postRepository.save(post);
    }

    @Transactional(readOnly = true)
    public List<PostDto.PostResponse> getAllPosts(User currentUser) {
        return postRepository.findAllByOrderByCreatedAtDesc().stream()
                .map(post -> mapToPostResponse(post, currentUser))
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<PostDto.PostResponse> getPostsByUser(User user, User currentUser) {
        return postRepository.findByUserIdOrderByCreatedAtDesc(user.getId()).stream()
                .map(post -> mapToPostResponse(post, currentUser))
                .collect(Collectors.toList());
    }

    // Helper to convert Entity -> DTO
    private PostDto.PostResponse mapToPostResponse(Post post, User currentUser) {
        PostDto.PostResponse response = new PostDto.PostResponse();
        response.setId(post.getId());
        response.setContent(post.getContent());
        response.setImageUrl(post.getImageUrl());
        response.setScore(post.getScore());
        response.setCommentsCount(post.getCommentsCount());
        response.setCreatedAt(post.getCreatedAt());
        response.setAuthorUsername(post.getUser().getDisplayName());
        response.setAuthorAvatarUrl(post.getUser().getAvatarUrl());

        // Check if the current user has already voted on this post
        if (currentUser != null) {
            postVoteRepository.findTopByPostAndUser(post, currentUser)
                    .ifPresent(vote -> response.setCurrentUserVote(vote.getVoteType().getDirection()));
        }

        return response;
    }
}