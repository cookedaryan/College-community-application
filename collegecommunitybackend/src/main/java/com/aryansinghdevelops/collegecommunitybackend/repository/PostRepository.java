package com.aryansinghdevelops.collegecommunitybackend.repository;

import com.aryansinghdevelops.collegecommunitybackend.model.Post;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.UUID; // <-- Import UUID

public interface PostRepository extends JpaRepository<Post, Long> { // Post ID is still Long

    // Change the parameter type for the user's ID to UUID
    List<Post> findByUserIdOrderByCreatedAtDesc(UUID userId);
    List<Post> findAllByOrderByCreatedAtDesc();
}