package com.aryansinghdevelops.collegecommunitybackend.service;

import com.aryansinghdevelops.collegecommunitybackend.dto.PostDto;
import com.aryansinghdevelops.collegecommunitybackend.model.Post;
import com.aryansinghdevelops.collegecommunitybackend.model.User;
import com.aryansinghdevelops.collegecommunitybackend.repository.PostRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PostService {

    private final PostRepository postRepository;

    @Transactional
    public PostDto.PostResponse createPost(PostDto.PostCreateRequest request, User currentUser) {
        Post newPost = new Post();
        newPost.setContent(request.getContent());
        newPost.setImageUrl(request.getImageUrl());
        newPost.setUser(currentUser);

        Post savedPost = postRepository.save(newPost);
        return mapToPostResponse(savedPost);
    }

    @Transactional(readOnly = true)
    public List<PostDto.PostResponse> getAllPosts() {
        return postRepository.findAllByOrderByCreatedAtDesc().stream()
                .map(this::mapToPostResponse)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<PostDto.PostResponse> getPostsByUser(User user) {
        return postRepository.findByUserIdOrderByCreatedAtDesc(user.getId()).stream()
                .map(this::mapToPostResponse)
                .collect(Collectors.toList());
    }

    private PostDto.PostResponse mapToPostResponse(Post post) {
        PostDto.PostResponse response = new PostDto.PostResponse();
        response.setId(post.getId());
        response.setContent(post.getContent());
        response.setImageUrl(post.getImageUrl());
        response.setLikesCount(post.getLikesCount());
        response.setCommentsCount(post.getCommentsCount());
        response.setCreatedAt(post.getCreatedAt());
        response.setAuthorUsername(post.getUser().getDisplayName());
        response.setAuthorAvatarUrl(post.getUser().getAvatarUrl()); // <-- ADDED
        return response;
    }
}