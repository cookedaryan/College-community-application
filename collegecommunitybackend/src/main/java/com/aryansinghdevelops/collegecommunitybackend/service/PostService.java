package com.aryansinghdevelops.collegecommunitybackend.service;

import com.aryansinghdevelops.collegecommunitybackend.dto.PostDto;
import com.aryansinghdevelops.collegecommunitybackend.model.*;
import com.aryansinghdevelops.collegecommunitybackend.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PostService {

    private final PostRepository postRepository;
    private final PostVoteRepository postVoteRepository;
    private final ClubRepository clubRepository;
    private final ClubMemberRepository clubMemberRepository;

    @Transactional
    public PostDto.PostResponse createPost(PostDto.PostCreateRequest request, User currentUser) {
        Post newPost = new Post();
        newPost.setContent(request.getContent());
        newPost.setImageUrl(request.getImageUrl());
        newPost.setUser(currentUser);

        if (request.getClubId() != null) {
            Club club = clubRepository.findById(request.getClubId())
                    .orElseThrow(() -> new RuntimeException("Club not found"));

            boolean isGlobalAdmin = currentUser.getRole() == Role.OWNER || currentUser.getRole() == Role.ADMIN;
            if (!isGlobalAdmin) {
                ClubMember member = clubMemberRepository.findByClubAndUser(club, currentUser)
                        .orElseThrow(() -> new RuntimeException("You are not a member of this club"));
                if (member.getRole() == ClubRole.MEMBER) {
                    throw new RuntimeException("Only Admins and Co-Admins can post to the club feed.");
                }
            }
            newPost.setClub(club);
        }

        Post savedPost = postRepository.save(newPost);
        return mapToPostResponse(savedPost, 0, currentUser);
    }

    // --- NEW: Delete Post ---
    @Transactional
    public void deletePost(Long postId, User currentUser) {
        Post post = postRepository.findById(postId).orElseThrow(() -> new RuntimeException("Post not found"));

        boolean isAuthor = post.getUser().getId().equals(currentUser.getId());
        boolean isGlobalOwner = currentUser.getRole() == Role.OWNER;

        if (isAuthor || isGlobalOwner) {
            postRepository.delete(post);
            return;
        }

        // Allow Club Admins to delete posts in their club
        if (post.getClub() != null) {
            clubMemberRepository.findByClubAndUser(post.getClub(), currentUser).ifPresent(member -> {
                if (member.getRole() == ClubRole.ADMIN) postRepository.delete(post);
                else throw new SecurityException("Not authorized");
            });
        } else {
            throw new SecurityException("Not authorized to delete this post");
        }
    }

    @Transactional
    public void vote(Long postId, VoteType voteType, User currentUser) {
        Post post = postRepository.findById(postId).orElseThrow(() -> new RuntimeException("Post not found"));
        Optional<PostVote> existingVote = postVoteRepository.findTopByPostAndUser(post, currentUser);

        if (existingVote.isPresent()) {
            PostVote vote = existingVote.get();
            if (vote.getVoteType().equals(voteType)) {
                postVoteRepository.delete(vote);
                post.setScore(post.getScore() - voteType.getDirection());
            } else {
                post.setScore(post.getScore() - vote.getVoteType().getDirection() + voteType.getDirection());
                vote.setVoteType(voteType);
                postVoteRepository.save(vote);
            }
        } else {
            PostVote newVote = PostVote.builder().post(post).user(currentUser).voteType(voteType).build();
            postVoteRepository.save(newVote);
            post.setScore(post.getScore() + voteType.getDirection());
        }
        postRepository.save(post);
    }

    @Transactional(readOnly = true)
    public List<PostDto.PostResponse> getAllPosts(int page, int size, User currentUser) {
        Pageable pageable = PageRequest.of(page, size);
        Page<Post> postPage = postRepository.findAllByOrderByCreatedAtDesc(pageable);
        return mapPosts(postPage.getContent(), currentUser);
    }

    @Transactional(readOnly = true)
    public List<PostDto.PostResponse> getPostsByUser(User user, int page, int size, User currentUser) {
        Pageable pageable = PageRequest.of(page, size);
        Page<Post> postPage = postRepository.findByUserIdOrderByCreatedAtDesc(user.getId(), pageable);
        return mapPosts(postPage.getContent(), currentUser);
    }

    @Transactional(readOnly = true)
    public List<PostDto.PostResponse> getAllClubPosts(int page, int size, User currentUser) {
        Pageable pageable = PageRequest.of(page, size);
        Page<Post> postPage = postRepository.findByClubIdIsNotNullOrderByCreatedAtDesc(pageable);
        return mapPosts(postPage.getContent(), currentUser);
    }

    @Transactional(readOnly = true)
    public List<PostDto.PostResponse> getPostsByClub(Long clubId, int page, int size, User currentUser) {
        Pageable pageable = PageRequest.of(page, size);
        Page<Post> postPage = postRepository.findByClubIdOrderByCreatedAtDesc(clubId, pageable);
        return mapPosts(postPage.getContent(), currentUser);
    }

    private List<PostDto.PostResponse> mapPosts(List<Post> posts, User currentUser) {
        Map<Long, Integer> userVotes = getVotesMap(posts, currentUser);
        return posts.stream()
                .map(post -> mapToPostResponse(post, userVotes.getOrDefault(post.getId(), 0), currentUser))
                .collect(Collectors.toList());
    }

    private Map<Long, Integer> getVotesMap(List<Post> posts, User currentUser) {
        if (currentUser == null || posts.isEmpty()) return Collections.emptyMap();
        List<PostVote> votes = postVoteRepository.findByUserAndPostIn(currentUser, posts);
        return votes.stream().collect(Collectors.toMap(v -> v.getPost().getId(), v -> v.getVoteType().getDirection()));
    }

    // Inside PostService.java, find the mapToPostResponse method:

    private PostDto.PostResponse mapToPostResponse(Post post, int currentUserVote, User currentUser) {
        PostDto.PostResponse response = new PostDto.PostResponse();
        response.setId(post.getId());
        response.setContent(post.getContent());
        response.setImageUrl(post.getImageUrl());
        response.setScore(post.getScore());
        response.setCommentsCount(post.getCommentsCount());
        response.setCreatedAt(post.getCreatedAt());
        response.setAuthorUsername(post.getUser().getDisplayName());
        response.setAuthorAvatarUrl(post.getUser().getAvatarUrl());
        response.setCurrentUserVote(currentUserVote);
        response.setAuthorScholarId(post.getUser().getScholarId());

        // --- UPDATED LOGIC ---
        if (currentUser != null) {
            // Check if IDs match
            boolean match = post.getUser().getId().equals(currentUser.getId());
            response.setAuthor(match);
        } else {
            response.setAuthor(false);
        }
        // ---------------------

        if (post.getClub() != null) {
            response.setClubName(post.getClub().getName());
            response.setClubId(post.getClub().getId());
        }
        return response;
    }
}