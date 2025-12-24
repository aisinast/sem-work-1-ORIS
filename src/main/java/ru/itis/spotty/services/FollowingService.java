package ru.itis.spotty.services;

import java.util.UUID;

public interface FollowingService {
    int countFollowers(UUID userId);
    int countFollowedToId(UUID userId);

    void follow(UUID userId, UUID followedToId);
    void unfollow(UUID userId, UUID followedToId);

    boolean isFollowing(UUID userId, UUID followedToId);
}
