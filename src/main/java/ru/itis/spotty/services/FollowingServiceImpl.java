package ru.itis.spotty.services;

import ru.itis.spotty.repositories.FollowingRepository;

import java.util.UUID;

public class FollowingServiceImpl implements FollowingService {

    private FollowingRepository followingRepository;

    public FollowingServiceImpl(FollowingRepository followingRepository) {
        this.followingRepository = followingRepository;
    }

    @Override
    public int countFollowers(UUID userId) {
        return followingRepository.countFollowers(userId);
    }

    @Override
    public int countFollowedToId(UUID userId) {
        return followingRepository.countFollowedToId(userId);
    }

    @Override
    public void follow(UUID userId, UUID followedToId) {
        followingRepository.follow(userId, followedToId);
    }

    @Override
    public void unfollow(UUID userId, UUID followedToId) {
        followingRepository.unfollow(userId, followedToId);
    }

    @Override
    public boolean isFollowing(UUID userId, UUID followedToId) {
        return followingRepository.isFollowing(userId, followedToId);
    }
}
