package com.aryansinghdevelops.collegecommunitybackend.service;

import com.aryansinghdevelops.collegecommunitybackend.dto.PostDto;
import com.aryansinghdevelops.collegecommunitybackend.model.Post;
import com.aryansinghdevelops.collegecommunitybackend.model.PostVote;
import com.aryansinghdevelops.collegecommunitybackend.model.User;
import com.aryansinghdevelops.collegecommunitybackend.model.VoteType;
import com.aryansinghdevelops.collegecommunitybackend.repository.PostRepository;
import com.aryansinghdevelops.collegecommunitybackend.repository.PostVoteRepository;
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

    // ... (createPost and vote methods remain UNCHANGED) ...
    @Transactional
    public PostDto.PostResponse createPost(PostDto.PostCreateRequest request, User currentUser) {
        Post newPost = new Post();
        newPost.setContent(request.getContent());
        newPost.setImageUrl(request.getImageUrl());
        newPost.setUser(currentUser);
        Post savedPost = postRepository.save(newPost);
        return mapToPostResponse(savedPost, 0);
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

    // --- UPDATED: Accepts page and size ---
    @Transactional(readOnly = true)
    public List<PostDto.PostResponse> getAllPosts(int page, int size, User currentUser) {
        Pageable pageable = PageRequest.of(page, size);
        Page<Post> postPage = postRepository.findAllByOrderByCreatedAtDesc(pageable);
        List<Post> posts = postPage.getContent();

        Map<Long, Integer> userVotes = getVotesMap(posts, currentUser);

        return posts.stream()
                .map(post -> mapToPostResponse(post, userVotes.getOrDefault(post.getId(), 0)))
                .collect(Collectors.toList());
    }

    // --- UPDATED: Accepts page and size ---
    @Transactional(readOnly = true)
    public List<PostDto.PostResponse> getPostsByUser(User user, int page, int size, User currentUser) {
        Pageable pageable = PageRequest.of(page, size);
        Page<Post> postPage = postRepository.findByUserIdOrderByCreatedAtDesc(user.getId(), pageable);
        List<Post> posts = postPage.getContent();

        Map<Long, Integer> userVotes = getVotesMap(posts, currentUser);

        return posts.stream()
                .map(post -> mapToPostResponse(post, userVotes.getOrDefault(post.getId(), 0)))
                .collect(Collectors.toList());
    }

    private Map<Long, Integer> getVotesMap(List<Post> posts, User currentUser) {
        if (currentUser == null || posts.isEmpty()) return Collections.emptyMap();
        List<PostVote> votes = postVoteRepository.findByUserAndPostIn(currentUser, posts);
        return votes.stream().collect(Collectors.toMap(v -> v.getPost().getId(), v -> v.getVoteType().getDirection()));
    }

    private PostDto.PostResponse mapToPostResponse(Post post, int currentUserVote) {
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
        return response;
    }
}