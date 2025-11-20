package com.aryansinghdevelops.collegecommunitybackend.service;

import com.aryansinghdevelops.collegecommunitybackend.dto.CommentDto;
import com.aryansinghdevelops.collegecommunitybackend.model.Comment;
import com.aryansinghdevelops.collegecommunitybackend.model.Post;
import com.aryansinghdevelops.collegecommunitybackend.model.User;
import com.aryansinghdevelops.collegecommunitybackend.repository.CommentRepository;
import com.aryansinghdevelops.collegecommunitybackend.repository.PostRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CommentService {

    private final CommentRepository commentRepository;
    private final PostRepository postRepository;

    @Transactional
    public CommentDto addComment(Long postId, CommentDto.CreateRequest request, User currentUser) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new RuntimeException("Post not found"));

        Comment comment = new Comment();
        comment.setContent(request.getContent());
        comment.setUser(currentUser);
        comment.setPost(post);

        // If this is a reply, set the parent
        if (request.getParentId() != null) {
            Comment parent = commentRepository.findById(request.getParentId())
                    .orElseThrow(() -> new RuntimeException("Parent comment not found"));
            comment.setParentComment(parent);
        }

        Comment savedComment = commentRepository.save(comment);

        // Update post comment count
        post.setCommentsCount(post.getCommentsCount() + 1);
        postRepository.save(post);

        return mapToDto(savedComment);
    }

    @Transactional(readOnly = true)
    public List<CommentDto> getCommentsForPost(Long postId) {
        List<Comment> comments = commentRepository.findByPostIdAndParentCommentIsNullOrderByCreatedAtDesc(postId);
        return comments.stream().map(this::mapToDto).collect(Collectors.toList());
    }

    // Recursive mapper to convert Comment entity to DTO
    private CommentDto mapToDto(Comment comment) {
        CommentDto dto = new CommentDto();
        dto.setId(comment.getId());
        dto.setContent(comment.getContent());
        dto.setAuthorUsername(comment.getUser().getDisplayName());
        dto.setAuthorAvatarUrl(comment.getUser().getAvatarUrl());
        dto.setCreatedAt(comment.getCreatedAt());

        // Recursively map replies
        if (comment.getReplies() != null) {
            dto.setReplies(comment.getReplies().stream()
                    .map(this::mapToDto)
                    .collect(Collectors.toList()));
        }
        return dto;
    }
}