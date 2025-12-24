package ru.itis.spotty.models;

import java.util.UUID;

public class Tag {
    private UUID tagId;
    private String tagName;
    private String category;

    public Tag(UUID tagId, String tagName, String category) {
        this.tagId = tagId;
        this.tagName = tagName;
        this.category = category;
    }

    public UUID getTagId() {
        return tagId;
    }

    public void setTagId(UUID tagId) {
        this.tagId = tagId;
    }

    public String getTagName() {
        return tagName;
    }

    public void setTagName(String tagName) {
        this.tagName = tagName;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }
}

