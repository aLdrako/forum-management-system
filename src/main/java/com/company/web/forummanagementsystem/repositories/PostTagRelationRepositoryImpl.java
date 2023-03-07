package com.company.web.forummanagementsystem.repositories;

import com.company.web.forummanagementsystem.models.PostTagRelation;
import com.company.web.forummanagementsystem.models.Tag;
import com.company.web.forummanagementsystem.models.TagId;
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
