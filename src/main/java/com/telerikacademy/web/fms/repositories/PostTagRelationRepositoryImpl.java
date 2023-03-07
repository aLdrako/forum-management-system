package com.telerikacademy.web.fms.repositories;

import com.telerikacademy.web.fms.models.PostTagRelation;
import com.telerikacademy.web.fms.models.Tag;
import com.telerikacademy.web.fms.models.TagId;
import com.telerikacademy.web.fms.repositories.contracts.PostTagRelationRepository;
import com.telerikacademy.web.fms.repositories.contracts.TagRepository;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.springframework.stereotype.Repository;

@Repository
public class PostTagRelationRepositoryImpl implements PostTagRelationRepository {

    private final SessionFactory sessionFactory;
    private final TagRepository tagRepository;

    public PostTagRelationRepositoryImpl(SessionFactory sessionFactory, TagRepository tagRepository) {
        this.sessionFactory = sessionFactory;
        this.tagRepository = tagRepository;
    }

    @Override
    public PostTagRelation getRelationById(Long postId, Long tagId) {
        Tag tag = tagRepository.getTagById(tagId);
        TagId id = new TagId(postId, tag);
        try (Session session = sessionFactory.openSession()){
            return session.get(PostTagRelation.class, id);
        }
    }

    @Override
    public void createRelation(PostTagRelation postTagRelation) {
        try (Session session = sessionFactory.openSession()){
            session.beginTransaction();
            session.persist(postTagRelation);
            session.getTransaction().commit();
        }
    }

    @Override
    public void deleteRelation(PostTagRelation postTagRelation) {
        try (Session session = sessionFactory.openSession()){
            session.beginTransaction();
            session.remove(postTagRelation);
            session.getTransaction().commit();
        }
    }

}
