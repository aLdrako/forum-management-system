package com.telerikacademy.web.fms.services.contracts;


import com.telerikacademy.web.fms.models.Tag;
import com.telerikacademy.web.fms.models.User;

import java.util.List;

public interface TagServices {
    Tag getTagById(Long id, User user);

    Tag createTag(String tagName);

    void delete(Long id, User user);

    List<Tag> getAll(User user);
}
