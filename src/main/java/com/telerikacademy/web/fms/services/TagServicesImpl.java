package com.telerikacademy.web.fms.services;


import com.telerikacademy.web.fms.exceptions.EntityNotFoundException;
import com.telerikacademy.web.fms.models.Tag;
import com.telerikacademy.web.fms.repositories.contracts.TagRepository;
import com.telerikacademy.web.fms.services.contracts.TagServices;
import org.springframework.stereotype.Service;

@Service
public class TagServicesImpl implements TagServices {

    private final TagRepository tagRepository;

    public TagServicesImpl(TagRepository tagRepository) {
        this.tagRepository = tagRepository;
    }

    @Override
    public Tag getTagById(Long id) {
        return tagRepository.getTagById(id);
    }

    @Override
    public Tag createTag(String tagName) {
        Tag tag = new Tag(tagName.split("[,]+", 2)[0].toLowerCase());
        try {
            return tagRepository.getTagByName(tag.getName());
        } catch (EntityNotFoundException e) {
            tagRepository.createTag(tag);
        }
        return tag;
    }
}
