package com.aryansinghdevelops.collegecommunitybackend.repository;

import com.aryansinghdevelops.collegecommunitybackend.model.Comment;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface CommentRepository extends JpaRepository<Comment, Long> {
    // Fetch only top-level comments (where parent is null) for a post
    List<Comment> findByPostIdAndParentCommentIsNullOrderByCreatedAtDesc(Long postId);
}