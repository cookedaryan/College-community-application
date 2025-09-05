package com.aryansinghdevelops.collegecommunitybackend.service;

import com.aryansinghdevelops.collegecommunitybackend.dto.PostDto;
import com.aryansinghdevelops.collegecommunitybackend.model.Post;
import com.aryansinghdevelops.collegecommunitybackend.model.User;
import com.aryansinghdevelops.collegecommunitybackend.repository.PostRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional; // <-- ADD THIS IMPORT

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PostService {

    private final PostRepository postRepository;

    @Transactional // Good practice for write operations
    public PostDto.PostResponse createPost(PostDto.PostCreateRequest request, User currentUser) {
        Post newPost = new Post();
        newPost.setContent(request.getContent());
        newPost.setImageUrl(request.getImageUrl());
        newPost.setUser(currentUser);

        Post savedPost = postRepository.save(newPost);
        return mapToPostResponse(savedPost);
    }

    // <-- ADD THIS ANNOTATION
    @Transactional(readOnly = true)
    public List<PostDto.PostResponse> getAllPosts() {
        return postRepository.findAllByOrderByCreatedAtDesc().stream()
                .map(this::mapToPostResponse)
                .collect(Collectors.toList());
    }

    // <-- ADD THIS ANNOTATION
    @Transactional(readOnly = true)
    public List<PostDto.PostResponse> getPostsByUser(User user) {
        return postRepository.findByUserIdOrderByCreatedAtDesc(user.getId()).stream()
                .map(this::mapToPostResponse)
                .collect(Collectors.toList());
    }

    // Helper method to convert a Post entity to a PostResponse DTO
    private PostDto.PostResponse mapToPostResponse(Post post) {
        PostDto.PostResponse response = new PostDto.PostResponse();
        response.setId(post.getId());
        response.setContent(post.getContent());
        response.setImageUrl(post.getImageUrl());
        response.setLikesCount(post.getLikesCount());
        response.setCommentsCount(post.getCommentsCount());
        response.setCreatedAt(post.getCreatedAt());
        response.setAuthorUsername(post.getUser().getDisplayName());
        return response;
    }
}