package com.aryansinghdevelops.collegecommunitybackend.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserDto {
    private String username;
    private String avatarUrl;
    private String bio;
    private String skills;
    private String role; // <-- ADDED THIS FIELD
}