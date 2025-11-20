package com.aryansinghdevelops.collegecommunitybackend.dto;

import lombok.Data;
import java.time.OffsetDateTime;
import java.util.List;

@Data
public class CommentDto {
    private Long id;
    private String content;
    private String authorUsername;
    private String authorAvatarUrl;
    private OffsetDateTime createdAt;
    private List<CommentDto> replies; // List of replies inside a comment

    // Request DTO for creating a comment
    @Data
    public static class CreateRequest {
        private String content;
        private Long parentId; // Optional: ID of the comment we are replying to
    }
}