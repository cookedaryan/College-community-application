package com.aryansinghdevelops.collegecommunitybackend.dto;

import lombok.Data;
import java.util.List;

@Data
public class ProfileResponseDto {
    private String username;
    private String avatarUrl;
    private int postCount;
    private int followerCount;
    private int followingCount;
    private boolean isFollowing;
    private List<PostDto.PostResponse> posts;
    private String bio;
    private String gender;
    private java.time.LocalDate dateOfBirth;
    private String skills;
}