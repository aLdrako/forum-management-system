package com.company.web.forummanagementsystem.repositories;

import com.company.web.forummanagementsystem.models.Tag;
import com.company.web.forummanagementsystem.models.TagId;

public interface TagRepository {
    Tag getTagById(Long id);
    Tag createTag(Tag tag);
    Tag getTagByName(String name);
}
