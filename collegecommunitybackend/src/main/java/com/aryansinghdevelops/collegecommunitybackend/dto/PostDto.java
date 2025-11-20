package com.aryansinghdevelops.collegecommunitybackend.dto;

import lombok.Data;
import java.time.OffsetDateTime;

public class PostDto {

    @Data
    public static class PostCreateRequest {
        private String content;
        private String imageUrl;
        private Long clubId;
    }

    @Data
    public static class PostResponse {
        private Long id;
        private String content;
        private String imageUrl;
        private int score;
        private int commentsCount;
        private int currentUserVote;
        private OffsetDateTime createdAt;
        private String authorUsername;
        private String authorAvatarUrl;
        private String clubName;
        private Long clubId;
        private Long authorScholarId;

        // --- RENAMED FIELD ---
        private boolean isAuthor; // Was 'ownPost', renamed for clarity
    }

    @Data
    public static class VoteRequest {
        private String voteType;
    }
}