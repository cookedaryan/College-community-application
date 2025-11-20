package com.aryansinghdevelops.collegecommunitybackend.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ClubDto {
    private Long id;
    private String name;
    private String description;
    private String imageUrl;
    private int memberCount; // Just send the count, not the whole list
}