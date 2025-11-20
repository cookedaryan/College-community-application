package com.aryansinghdevelops.collegecommunitybackend.repository;

import com.aryansinghdevelops.collegecommunitybackend.model.Post;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.UUID;

public interface PostRepository extends JpaRepository<Post, Long> {

    // CHANGED: Returns a Page instead of a List, accepts Pageable
    Page<Post> findAllByOrderByCreatedAtDesc(Pageable pageable);

    // Also update this one for the profile page
    Page<Post> findByUserIdOrderByCreatedAtDesc(UUID userId, Pageable pageable);
}