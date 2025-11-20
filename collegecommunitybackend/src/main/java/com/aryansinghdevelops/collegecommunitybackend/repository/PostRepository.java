package com.aryansinghdevelops.collegecommunitybackend.repository;

import com.aryansinghdevelops.collegecommunitybackend.model.Post;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.UUID;

public interface PostRepository extends JpaRepository<Post, Long> {

    // Home Feed (All posts, regardless of club)
    Page<Post> findAllByOrderByCreatedAtDesc(Pageable pageable);

    // Profile Feed
    Page<Post> findByUserIdOrderByCreatedAtDesc(UUID userId, Pageable pageable);

    // NEW: All Club Posts (Where club_id is NOT NULL)
    Page<Post> findByClubIdIsNotNullOrderByCreatedAtDesc(Pageable pageable);

    // NEW: Specific Club Posts
    Page<Post> findByClubIdOrderByCreatedAtDesc(Long clubId, Pageable pageable);
}