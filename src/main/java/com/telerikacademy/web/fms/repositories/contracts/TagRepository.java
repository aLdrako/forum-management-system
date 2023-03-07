package com.telerikacademy.web.fms.repositories.contracts;


import com.telerikacademy.web.fms.models.Tag;

public interface TagRepository {
    Tag getTagById(Long id);
    Tag createTag(Tag tag);
    Tag getTagByName(String name);
}
