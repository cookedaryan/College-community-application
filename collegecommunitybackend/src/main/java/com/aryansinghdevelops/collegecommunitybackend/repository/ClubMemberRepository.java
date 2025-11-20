package com.aryansinghdevelops.collegecommunitybackend.repository;

import com.aryansinghdevelops.collegecommunitybackend.model.Club;
import com.aryansinghdevelops.collegecommunitybackend.model.ClubMember;
import com.aryansinghdevelops.collegecommunitybackend.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface ClubMemberRepository extends JpaRepository<ClubMember, Long> {
    Optional<ClubMember> findByClubAndUser(Club club, User user);
}