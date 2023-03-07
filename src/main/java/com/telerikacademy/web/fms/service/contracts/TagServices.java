package com.telerikacademy.web.fms.service.contracts;


import com.telerikacademy.web.fms.models.Tag;

public interface TagServices {
    Tag getTagById(Long id);
    Tag createTag(Tag tag);
    Tag createTag(String tagName);
}
