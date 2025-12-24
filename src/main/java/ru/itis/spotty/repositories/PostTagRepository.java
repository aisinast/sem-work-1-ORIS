package ru.itis.spotty.repositories;

import ru.itis.spotty.models.Tag;

import java.util.List;
import java.util.UUID;

public interface PostTagRepository {
    List<Tag> getPostTags(UUID post_id);
    void addPostTag(UUID post_id, UUID tag_id);
    void deleteAllPostTags(UUID post_id);
}
