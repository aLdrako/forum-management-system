package com.telerikacademy.web.fms.repositories;

import com.telerikacademy.web.fms.models.Tag;
import com.telerikacademy.web.fms.repositories.contracts.TagRepository;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.query.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class TagRepositoryImpl implements TagRepository {

    private final SessionFactory sessionFactory;

    public TagRepositoryImpl(SessionFactory sessionFactory){
        this.sessionFactory = sessionFactory;
    }

    @Override
    public Tag getTagById(Long id) {
        try (Session session = sessionFactory.openSession()){
            return session.get(Tag.class, id);
        }
    }

    @Override
    public Tag createTag(Tag tag) {
        try (Session session = sessionFactory.openSession()){
            session.beginTransaction();
            session.persist(tag);
            session.getTransaction().commit();
        }
        return tag;
    }

    @Override
    public Tag getTagByName(String name) {
        try (Session session = sessionFactory.openSession()){
            Query<Tag> query = session.createQuery("from Tag t where t.name = :tagName", Tag.class);
            query.setParameter("tagName", name);
            List<Tag> result = query.list();
            if (!result.isEmpty()) {
                return result.get(0);
            }
            return null;
        }
    }
}
