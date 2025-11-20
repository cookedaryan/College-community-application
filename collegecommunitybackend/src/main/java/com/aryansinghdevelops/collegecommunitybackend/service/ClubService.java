package com.aryansinghdevelops.collegecommunitybackend.service;

import com.aryansinghdevelops.collegecommunitybackend.dto.ClubDto;
import com.aryansinghdevelops.collegecommunitybackend.model.*;
import com.aryansinghdevelops.collegecommunitybackend.repository.ClubMemberRepository;
import com.aryansinghdevelops.collegecommunitybackend.repository.ClubRepository;
import com.aryansinghdevelops.collegecommunitybackend.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ClubService {

    private final ClubRepository clubRepository;
    private final ClubMemberRepository clubMemberRepository;
    private final UserRepository userRepository;

    @Transactional
    public ClubDto createClub(String name, String description, String imageUrl, User currentUser) {
        if (currentUser.getRole() != Role.OWNER) {
            throw new SecurityException("Only the App Owner can create clubs.");
        }

        Club club = Club.builder()
                .name(name)
                .description(description)
                .imageUrl(imageUrl) // <-- Added Image URL support
                .build();

        Club savedClub = clubRepository.save(club);
        return mapToDto(savedClub);
    }

    // --- NEW: Update Club Method ---
    @Transactional
    public ClubDto updateClub(Long clubId, String name, String description, String imageUrl, User currentUser) {
        Club club = clubRepository.findById(clubId)
                .orElseThrow(() -> new RuntimeException("Club not found"));

        // Permission Check: Owner, Admin, or Co-Admin
        boolean isGlobalOwner = currentUser.getRole() == Role.OWNER;

        if (!isGlobalOwner) {
            ClubMember member = clubMemberRepository.findByClubAndUser(club, currentUser)
                    .orElseThrow(() -> new SecurityException("You are not a member of this club."));

            if (member.getRole() == ClubRole.MEMBER) {
                throw new SecurityException("Only Admins and Co-Admins can update club details.");
            }
        }

        // Update fields if provided
        if (name != null && !name.isBlank()) club.setName(name);
        if (description != null) club.setDescription(description);
        if (imageUrl != null) club.setImageUrl(imageUrl);

        Club savedClub = clubRepository.save(club);
        return mapToDto(savedClub);
    }

    @Transactional(readOnly = true)
    public List<ClubDto> getAllClubs() {
        return clubRepository.findAll().stream()
                .map(this::mapToDto)
                .collect(Collectors.toList());
    }

    private ClubDto mapToDto(Club club) {
        return new ClubDto(
                club.getId(),
                club.getName(),
                club.getDescription(),
                club.getImageUrl(),
                club.getMembers() != null ? club.getMembers().size() : 0
        );
    }

    @Transactional
    public void assignClubAdmin(Long clubId, String usernameToPromote, User currentUser) {
        if (currentUser.getRole() != Role.OWNER) {
            throw new SecurityException("Only the App Owner can assign Club Admins.");
        }
        Club club = clubRepository.findById(clubId).orElseThrow(() -> new RuntimeException("Club not found"));
        User userToPromote = userRepository.findByEmail(usernameToPromote).orElseThrow(() -> new RuntimeException("User not found"));
        assignRole(club, userToPromote, ClubRole.ADMIN);
    }

    @Transactional
    public void assignCoAdmin(Long clubId, String usernameToPromote, User currentUser) {
        Club club = clubRepository.findById(clubId).orElseThrow(() -> new RuntimeException("Club not found"));
        ClubMember currentMember = clubMemberRepository.findByClubAndUser(club, currentUser)
                .orElseThrow(() -> new SecurityException("You are not a member of this club."));

        if (currentMember.getRole() != ClubRole.ADMIN) {
            throw new SecurityException("Only the Club Admin can assign Co-Admins.");
        }

        long coAdminCount = club.getMembers().stream()
                .filter(m -> m.getRole() == ClubRole.CO_ADMIN)
                .count();

        if (coAdminCount >= 2) {
            throw new IllegalStateException("This club already has 2 Co-Admins.");
        }

        User userToPromote = userRepository.findByEmail(usernameToPromote)
                .orElseThrow(() -> new RuntimeException("User not found"));
        assignRole(club, userToPromote, ClubRole.CO_ADMIN);
    }

    private void assignRole(Club club, User user, ClubRole role) {
        ClubMember member = clubMemberRepository.findByClubAndUser(club, user)
                .orElse(ClubMember.builder().club(club).user(user).build());
        member.setRole(role);
        clubMemberRepository.save(member);
    }
}