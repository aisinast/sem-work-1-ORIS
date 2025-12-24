package ru.itis.spotty.models;

public class Follow {
    private String followerId;
    private String followedToId;

    public Follow(String followerId, String followedToId) {
        this.followerId = followerId;
        this.followedToId = followedToId;
    }

    public String getFollowerId() {
        return followerId;
    }

    public void setFollowerId(String followerId) {
        this.followerId = followerId;
    }

    public String getFollowedToId() {
        return followedToId;
    }

    public void setFollowedToId(String followedToId) {
        this.followedToId = followedToId;
    }
}

