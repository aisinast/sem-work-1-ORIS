package ru.itis.spotty.models;

import java.util.List;

public class PostPage {
    private final List<Post> posts;
    private final int totalPosts;
    private final int currentPage;
    private final int totalPages;

    public PostPage(List<Post> posts, int totalPosts, int currentPage, int totalPages) {
        this.posts = posts;
        this.totalPosts = totalPosts;
        this.currentPage = currentPage;
        this.totalPages = totalPages;
    }

    public List<Post> getPosts() {
        return posts;
    }

    public int getTotalPosts() {
        return totalPosts;
    }

    public int getCurrentPage() {
        return currentPage;
    }

    public int getTotalPages() {
        return totalPages;
    }
}

