package com.telerikacademy.web.fms.repositories.contracts;


import com.telerikacademy.web.fms.models.PostTagRelation;

public interface PostTagRelationRepository {

    PostTagRelation getRelationById(Long postId, Long tagId);

    void createRelation(PostTagRelation postTagRelation);

    void deleteRelation(PostTagRelation postTagRelation);
}
