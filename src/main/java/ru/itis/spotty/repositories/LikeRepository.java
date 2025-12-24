package ru.itis.spotty.repositories;

import ru.itis.spotty.models.Post;

import java.util.List;
import java.util.UUID;

public interface LikeRepository {
    void addLike(UUID postId, UUID userId);
    void removeLike(UUID postId, UUID userId);
    int countPostLikes(UUID postId);
    boolean isCurrentUserLiked(UUID postId, UUID userId);
    List<Post> getLikedPosts(UUID userId);
}
