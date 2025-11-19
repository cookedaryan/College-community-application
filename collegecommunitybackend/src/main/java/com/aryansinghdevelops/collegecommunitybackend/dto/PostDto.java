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
        private int likesCount;
        private int commentsCount;
        private OffsetDateTime createdAt;
        private String authorUsername;
        private String authorAvatarUrl; // <-- ADDED
    }
}