package com.aryansinghdevelops.collegecommunitybackend.model;

import lombok.Getter;

@Getter
public enum VoteType {
    UPVOTE(1),
    DOWNVOTE(-1);

    private int direction;

    VoteType(int direction) {
        this.direction = direction;
    }

}