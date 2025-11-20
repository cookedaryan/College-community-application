package com.aryansinghdevelops.collegecommunitybackend.repository;

import com.aryansinghdevelops.collegecommunitybackend.model.Post;
import com.aryansinghdevelops.collegecommunitybackend.model.PostVote;
import com.aryansinghdevelops.collegecommunitybackend.model.User;
import com.aryansinghdevelops.collegecommunitybackend.model.VoteType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface PostVoteRepository extends JpaRepository<PostVote, UUID> {
    Optional<PostVote> findTopByPostAndUser(Post post, User user);

    @Query("SELECT COUNT(v) FROM PostVote v WHERE v.post = :post AND v.voteType = :type")
    int countByPostAndVoteType(@Param("post") Post post, @Param("type") VoteType type);

    // --- NEW OPTIMIZED METHOD ---
    // Fetches all votes by this user for a specific list of posts in ONE go
    List<PostVote> findByUserAndPostIn(User user, List<Post> posts);
}