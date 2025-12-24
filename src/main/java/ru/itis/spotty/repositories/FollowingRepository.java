package ru.itis.spotty.repositories;

import java.util.UUID;

public interface FollowingRepository {
    int countFollowers(UUID userId);
    int countFollowedToId(UUID userId);

    void follow(UUID userId, UUID followedToId);
    void unfollow(UUID userId, UUID followedToId);

    boolean isFollowing(UUID userId, UUID followedToId);
}
