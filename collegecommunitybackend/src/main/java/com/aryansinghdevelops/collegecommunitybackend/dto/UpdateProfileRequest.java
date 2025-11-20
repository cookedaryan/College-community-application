package com.aryansinghdevelops.collegecommunitybackend.dto;

import lombok.Data;
import java.time.LocalDate;

@Data
public class UpdateProfileRequest {
    private String username; // Display name
    private String bio;
    private String gender;
    private LocalDate dateOfBirth;
    private String skills; // Comma-separated string
    private String avatarUrl; // Optional: if they want to change pic
}