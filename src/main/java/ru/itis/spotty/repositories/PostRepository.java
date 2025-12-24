package ru.itis.spotty.repositories;

import ru.itis.spotty.models.Post;

import java.util.List;
import java.util.UUID;

public interface PostRepository {
    UUID addNewPost(Post post);
    void updatePost(Post post);
    void deletePost(Post post);

    Post getPostById(UUID id);
    List<Post> getPostsByUserId(UUID id);
    int getUserPostsCount(UUID id);

    List<Post> getPostsByPlaceId(UUID placeId);

    List<Post> getPostsForPostPage(int limit, int offset);
    int getTotalPostsCount();
}
