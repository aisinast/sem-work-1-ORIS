package ru.itis.spotty.repositories;

import ru.itis.spotty.models.Tag;

import java.util.List;

public interface TagRepository {
    List<Tag> getAllTags();
}
