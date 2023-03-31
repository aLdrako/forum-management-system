package com.telerikacademy.web.fms.repositories.contracts;


import com.telerikacademy.web.fms.models.Tag;

import java.util.List;

public interface TagRepository {
    Tag getTagById(Long id);
    Tag createTag(Tag tag);
    Tag getTagByName(String name);

    void delete(Tag tag);

    List<Tag> getAll();

}
