package ru.itis.spotty.models;

import java.util.UUID;

public class PostTag {
    private UUID postId;
    private UUID tagId;

    public PostTag(UUID postId, UUID tagId) {
        this.postId = postId;
        this.tagId = tagId;
    }

    public UUID getPostId() {
        return postId;
    }

    public void setPostId(UUID postId) {
        this.postId = postId;
    }

    public UUID getTagId() {
        return tagId;
    }
    public void setTagId(UUID tagId) {
        this.tagId = tagId;
    }
}
