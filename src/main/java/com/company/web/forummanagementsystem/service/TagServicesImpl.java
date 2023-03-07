package com.company.web.forummanagementsystem.service;

import com.company.web.forummanagementsystem.models.Tag;
import com.company.web.forummanagementsystem.models.TagId;
import com.company.web.forummanagementsystem.repositories.TagRepository;
import jakarta.websocket.server.ServerEndpoint;
import org.springframework.stereotype.Service;

@Service
public class TagServicesImpl implements TagServices{

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
