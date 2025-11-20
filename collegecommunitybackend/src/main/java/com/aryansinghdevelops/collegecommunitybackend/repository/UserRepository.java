package com.aryansinghdevelops.collegecommunitybackend.repository;

import com.aryansinghdevelops.collegecommunitybackend.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;
import java.util.UUID;

public interface UserRepository extends JpaRepository<User, UUID> {

    // Used for login (via email)
    Optional<User> findByEmail(String email);

    // Used for login (via Scholar ID)
    Optional<User> findByScholarId(Long scholarId);

    // --- THIS WAS MISSING ---
    // Used to find user profiles by their display name
    Optional<User> findByUsername(String username);
}