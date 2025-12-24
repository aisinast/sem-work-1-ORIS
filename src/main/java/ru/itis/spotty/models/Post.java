package ru.itis.spotty.models;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public class Post {
    private UUID postId;
    private String title;
    private String content;
    private UUID authorId;
    private UUID placeId;
    private String imageUrl;
    private LocalDateTime createdAt;

    private int likes;
    private List<PostTag> postTags;

    private String placeName;
    private String authorUsername;
    private String formattedCreatedAt;
    private boolean isCurrentUserLiked;

    public Post(String title, String content, UUID authorId, UUID placeId, String imageUrl) {
        this.title = title;
        this.content = content;
        this.authorId = authorId;
        this.placeId = placeId;
        this.imageUrl = imageUrl;
    }

    public Post(UUID postId, String title, String content, UUID authorId, UUID placeId, String imageUrl, LocalDateTime createdAt) {
        this.postId = postId;
        this.title = title;
        this.content = content;
        this.authorId = authorId;
        this.placeId = placeId;
        this.imageUrl = imageUrl;
        this.createdAt = createdAt;
    }

    public UUID getPostId() {
        return postId;
    }

    public void setPostId(UUID postId) {
        this.postId = postId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public UUID getAuthorId() {
        return authorId;
    }

    public void setAuthorId(UUID authorId) {
        this.authorId = authorId;
    }

    public UUID getPlaceId() {
        return placeId;
    }

    public void setPlaceId(UUID placeId) {
        this.placeId = placeId;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public int getLikes() {
        return likes;
    }

    public void setLikes(int likes) {
        this.likes = likes;
    }

    public List<PostTag> getPostTags() {
        return postTags;
    }

    public void setPostTags(List<PostTag> postTags) {
        this.postTags = postTags;
    }

    public String getPlaceName() {
        return placeName;
    }

    public void setPlaceName(String placeName) {
        this.placeName = placeName;
    }

    public String getAuthorUsername() {
        return authorUsername;
    }

    public void setAuthorUsername(String authorUsername) {
        this.authorUsername = authorUsername;
    }

    public String getFormattedCreatedAt() {
        return formattedCreatedAt;
    }

    public void setFormattedCreatedAt(String formattedCreatedAt) {
        this.formattedCreatedAt = formattedCreatedAt;
    }

    public boolean isCurrentUserLiked() {
        return isCurrentUserLiked;
    }

    public void setCurrentUserLiked(boolean currentUserLiked) {
        isCurrentUserLiked = currentUserLiked;
    }
}
