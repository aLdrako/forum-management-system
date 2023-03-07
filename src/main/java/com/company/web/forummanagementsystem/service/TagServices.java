package com.company.web.forummanagementsystem.service;

import com.company.web.forummanagementsystem.models.Tag;
import com.company.web.forummanagementsystem.models.TagId;

public interface TagServices {
    Tag getTagById(Long id);
    Tag createTag(Tag tag);
    Tag createTag(String tagName);
}
