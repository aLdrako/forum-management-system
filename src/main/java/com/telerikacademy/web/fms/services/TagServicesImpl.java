package com.telerikacademy.web.fms.services;


import com.telerikacademy.web.fms.exceptions.EntityNotFoundException;
import com.telerikacademy.web.fms.exceptions.UnauthorizedOperationException;
import com.telerikacademy.web.fms.models.Post;
import com.telerikacademy.web.fms.models.Tag;
import com.telerikacademy.web.fms.models.User;
import com.telerikacademy.web.fms.repositories.contracts.TagRepository;
import com.telerikacademy.web.fms.services.contracts.TagServices;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;

@Service
public class TagServicesImpl implements TagServices {

    public static final String UNAUTHORIZED_MESSAGE = "Only admins can view/remove tags!";
    private final TagRepository tagRepository;

    public TagServicesImpl(TagRepository tagRepository) {
        this.tagRepository = tagRepository;
    }

    @Override
    public Tag getTagById(Long id, User user) {
        checkAuthorizedPermissions(user);
        return tagRepository.getTagById(id);
    }

    @Override
    public Tag createTag(String tagName) {
        Tag tag = new Tag(tagName.toLowerCase());
        try {
            return tagRepository.getTagByName(tag.getName());
        } catch (EntityNotFoundException e) {
            tagRepository.createTag(tag);
        }
        return tag;
    }

    @Override
    public void delete(Long id, User user) {
        checkAuthorizedPermissions(user);
        Tag tag = tagRepository.getTagById(id);
        tagRepository.delete(tag);
    }

    @Override
    public List<Tag> getAll(User user) {
        checkAuthorizedPermissions(user);
        return tagRepository.getAll();
    }

    private void checkAuthorizedPermissions(User user) {
        if (!user.getPermission().isAdmin()) {
            throw new UnauthorizedOperationException(UNAUTHORIZED_MESSAGE);
        }
    }
}
