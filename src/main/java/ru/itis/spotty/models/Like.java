package ru.itis.spotty.models;

import java.time.LocalDateTime;
import java.util.UUID;

public class Like {
    private UUID postId;
    private UUID userId;
    private LocalDateTime createdAt;

    public Like(UUID postId, UUID userId, LocalDateTime createdAt) {
        this.postId = postId;
        this.userId = userId;
        this.createdAt = createdAt;
    }

    public UUID getPostId() {
        return postId;
    }

    public void setPostId(UUID postId) {
        this.postId = postId;
    }

    public UUID getUserId() {
        return userId;
    }

    public void setUserId(UUID userId) {
        this.userId = userId;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
}
