package com.aryansinghdevelops.collegecommunitybackend.repository;

import com.aryansinghdevelops.collegecommunitybackend.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;
import java.util.UUID; // <-- Import UUID

public interface UserRepository extends JpaRepository<User, UUID> { // <-- Change Long to UUID
    Optional<User> findByEmail(String email);
    Optional<User> findByUsername(String username);
}