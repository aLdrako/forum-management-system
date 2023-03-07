package com.company.web.forummanagementsystem.repositories;

import com.company.web.forummanagementsystem.models.PostTagRelation;

public interface PostTagRelationRepository {

    PostTagRelation getRelationById(Long postId, Long tagId);

    void createRelation(PostTagRelation postTagRelation);

    void deleteRelation(PostTagRelation postTagRelation);
}
