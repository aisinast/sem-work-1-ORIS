package ru.itis.spotty.services;

import ru.itis.spotty.models.Post;
import ru.itis.spotty.models.PostPage;
import ru.itis.spotty.models.Tag;

import java.util.List;
import java.util.UUID;

public interface PostService {
    Post getPostById(UUID id);
    List<Post> getPostsByUserId(UUID id);
    int getPostsCount(UUID id);

    UUID addPost(Post post);
    void updatePost(Post post, UUID currentUserId);
    void deletePost(UUID id);

    List<Tag> getPostTags(UUID post_id);
    List<Tag> getAllTags();

    void addPostTag(UUID post_id, UUID tag_id);
    void updatePostTags(UUID post_id, List<UUID> tag_ids);

    List<Post> getPostsByPlaceId(UUID placeId);

    PostPage getPostPage(int page, int size);

    void addLike(UUID postId, UUID userId);
    void removeLike(UUID postId, UUID userId);
    int countPostLikes(UUID postId);
    boolean isCurrentUserLiked(UUID postId, UUID userId);
    List<Post> getUserLikedPosts(UUID userId);
}
