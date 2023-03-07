package com.telerikacademy.web.fms.service;


import com.telerikacademy.web.fms.models.Tag;
import com.telerikacademy.web.fms.repositories.contracts.TagRepository;
import com.telerikacademy.web.fms.service.contracts.TagServices;
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
    public Tag createTag(Tag tag) {
        Tag tagFromRepo = tagRepository.getTagByName(tag.getName());
        if (tagFromRepo == null) {
            return tagRepository.createTag(tag);
        }
        return tagFromRepo;
    }

    @Override
    public Tag createTag(String tagName) {
        Tag newTag = new Tag(tagName);
        return createTag(newTag);
    }
}
