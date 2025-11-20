package com.aryansinghdevelops.collegecommunitybackend.controller;

import com.aryansinghdevelops.collegecommunitybackend.dto.ClubDto;
import com.aryansinghdevelops.collegecommunitybackend.dto.PostDto;
import com.aryansinghdevelops.collegecommunitybackend.model.Role;
import com.aryansinghdevelops.collegecommunitybackend.model.User;
import com.aryansinghdevelops.collegecommunitybackend.repository.ClubMemberRepository;
import com.aryansinghdevelops.collegecommunitybackend.repository.ClubRepository;
import com.aryansinghdevelops.collegecommunitybackend.service.ClubService;
import com.aryansinghdevelops.collegecommunitybackend.service.PostService;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/clubs")
@RequiredArgsConstructor
public class ClubController {

    private final ClubService clubService;
    private final PostService postService;
    private final ClubMemberRepository clubMemberRepository;
    private final ClubRepository clubRepository;

    @GetMapping
    public ResponseEntity<List<ClubDto>> getAllClubs() {
        return ResponseEntity.ok(clubService.getAllClubs());
    }

    @PostMapping
    public ResponseEntity<ClubDto> createClub(
            @RequestBody CreateClubRequest request,
            @AuthenticationPrincipal User currentUser) {
        return ResponseEntity.ok(clubService.createClub(request.getName(), request.getDescription(), request.getImageUrl(), currentUser));
    }

    // --- NEW: Update Club Endpoint ---
    @PutMapping("/{clubId}")
    public ResponseEntity<ClubDto> updateClub(
            @PathVariable Long clubId,
            @RequestBody UpdateClubRequest request,
            @AuthenticationPrincipal User currentUser) {
        return ResponseEntity.ok(clubService.updateClub(clubId, request.getName(), request.getDescription(), request.getImageUrl(), currentUser));
    }

    @PostMapping("/{clubId}/admin")
    public ResponseEntity<?> assignAdmin(@PathVariable Long clubId, @RequestBody UserRequest request, @AuthenticationPrincipal User currentUser) {
        clubService.assignClubAdmin(clubId, request.getUsername(), currentUser);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/{clubId}/co-admin")
    public ResponseEntity<?> assignCoAdmin(@PathVariable Long clubId, @RequestBody UserRequest request, @AuthenticationPrincipal User currentUser) {
        clubService.assignCoAdmin(clubId, request.getUsername(), currentUser);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/posts")
    public ResponseEntity<List<PostDto.PostResponse>> getAllClubPosts(@AuthenticationPrincipal User currentUser, @RequestParam(defaultValue = "0") int page, @RequestParam(defaultValue = "10") int size) {
        return ResponseEntity.ok(postService.getAllClubPosts(page, size, currentUser));
    }

    @GetMapping("/{clubId}/posts")
    public ResponseEntity<List<PostDto.PostResponse>> getClubPosts(@PathVariable Long clubId, @AuthenticationPrincipal User currentUser, @RequestParam(defaultValue = "0") int page, @RequestParam(defaultValue = "10") int size) {
        return ResponseEntity.ok(postService.getPostsByClub(clubId, page, size, currentUser));
    }

    @GetMapping("/{clubId}/role")
    public ResponseEntity<String> getUserRoleInClub(@PathVariable Long clubId, @AuthenticationPrincipal User currentUser) {
        if (currentUser.getRole() == Role.OWNER) {
            return ResponseEntity.ok("ADMIN");
        }
        return clubMemberRepository.findByClubAndUser(clubRepository.getReferenceById(clubId), currentUser)
                .map(member -> ResponseEntity.ok(member.getRole().name()))
                .orElse(ResponseEntity.ok("NONE"));
    }

    @Data static class CreateClubRequest {
        private String name;
        private String description;
        private String imageUrl; // Added
    }

    @Data static class UpdateClubRequest {
        private String name;
        private String description;
        private String imageUrl;
    }

    @Data static class UserRequest { private String username; }
}