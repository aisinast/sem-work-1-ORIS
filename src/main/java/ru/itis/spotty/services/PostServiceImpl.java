package ru.itis.spotty.services;

import ru.itis.spotty.exceptions.AccessDeniedException;
import ru.itis.spotty.exceptions.NotFoundException;
import ru.itis.spotty.models.Post;
import ru.itis.spotty.models.PostPage;
import ru.itis.spotty.models.Tag;
import ru.itis.spotty.repositories.LikeRepository;
import ru.itis.spotty.repositories.PostRepository;
import ru.itis.spotty.repositories.PostTagRepository;
import ru.itis.spotty.repositories.TagRepository;

import java.util.List;
import java.util.UUID;

public class PostServiceImpl implements PostService {

    private PostRepository postRepository;
    private PostTagRepository postTagRepository;
    private TagRepository tagRepository;
    private LikeRepository likeRepository;

    public PostServiceImpl(PostRepository postRepository, PostTagRepository postTagRepository,
                           TagRepository tagRepository,  LikeRepository likeRepository) {
        this.postRepository = postRepository;
        this.postTagRepository = postTagRepository;
        this.tagRepository = tagRepository;
        this.likeRepository = likeRepository;
    }

    @Override
    public Post getPostById(UUID id) {
        Post post = postRepository.getPostById(id);

        if (post == null) {
            throw new NotFoundException("Пост не найден");
        }

        return post;
    }

    @Override
    public List<Post> getPostsByUserId(UUID id) {
        List<Post> posts = postRepository.getPostsByUserId(id);

        if (posts.isEmpty()) {
            throw new NotFoundException("Посты не найдены");
        }

        return posts;
    }

    @Override
    public int getPostsCount(UUID id) {
        return postRepository.getUserPostsCount(id);
    }

    @Override
    public UUID addPost(Post post) {
        UUID postId = postRepository.addNewPost(post);

        if (postId == null) {
            throw new RuntimeException("Произошла ошибка при создании поста");
        }

        return postId;
    }

    @Override
    public void updatePost(Post post, UUID currentUserId) {
        if (!post.getAuthorId().equals(currentUserId)) {
            throw new AccessDeniedException("Вы не можете отредактировать чужой пост");
        }

        postRepository.updatePost(post);
    }

    @Override
    public void deletePost(UUID id) {
        Post post = postRepository.getPostById(id);

        if (post == null) {
            throw new NotFoundException("Пост не найден");
        }

        postRepository.deletePost(post);
    }

    @Override
    public List<Tag> getPostTags(UUID post_id) {
        return postTagRepository.getPostTags(post_id);
    }

    @Override
    public List<Tag> getAllTags() {
        return tagRepository.getAllTags();
    }

    @Override
    public void addPostTag(UUID post_id, UUID tag_id) {
        postTagRepository.addPostTag(post_id, tag_id);
    }

    @Override
    public void updatePostTags(UUID post_id,  List<UUID> tag_ids) {
        postTagRepository.deleteAllPostTags(post_id);

        for (UUID tag_id : tag_ids) {
            postTagRepository.addPostTag(post_id, tag_id);
        }
    }

    @Override
    public List<Post> getPostsByPlaceId(UUID placeId) {
        return postRepository.getPostsByPlaceId(placeId);
    }

    @Override
    public PostPage getPostPage(int page, int size) {
        if (page <= 0) page = 1;
        if (size <= 0) size = 10;

        int offset = (page - 1) * size;
        List<Post> posts = postRepository.getPostsForPostPage(size, offset);
        int totalPosts = postRepository.getTotalPostsCount();
        int totalPages = (int) Math.ceil((double) totalPosts / size);

        return new PostPage(posts, totalPosts, page, totalPages);
    }

    @Override
    public void addLike(UUID postId, UUID userId) {
        likeRepository.addLike(postId, userId);
    }

    @Override
    public void removeLike(UUID postId, UUID userId) {
        likeRepository.removeLike(postId, userId);
    }

    @Override
    public int countPostLikes(UUID postId) {
        return likeRepository.countPostLikes(postId);
    }

    @Override
    public boolean isCurrentUserLiked(UUID postId, UUID userId) {
        return likeRepository.isCurrentUserLiked(postId, userId);
    }

    @Override
    public List<Post> getUserLikedPosts(UUID userId) {
        return likeRepository.getLikedPosts(userId);
    }
}
