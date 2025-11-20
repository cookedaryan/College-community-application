package com.aryansinghdevelops.collegecommunitybackend.dto;

import lombok.Data;
import java.time.OffsetDateTime;

public class PostDto {

    @Data
    public static class PostCreateRequest {
        private String content;
        private String imageUrl;
    }

    @Data
    public static class PostResponse {
        private Long id;
        private String content;
        private String imageUrl;
        private int commentsCount;

        // REPLACED likesCount with score
        private int score;

        // NEW: Tells the frontend if the user Upvoted (1), Downvoted (-1), or Neither (0)
        private int currentUserVote;

        private OffsetDateTime createdAt;
        private String authorUsername;
        private String authorAvatarUrl;
    }

    // NEW: DTO for handling vote requests
    @Data
    public static class VoteRequest {
        private String voteType; // Expects "UPVOTE" or "DOWNVOTE"
    }
}