package com.company.web.forummanagementsystem.repositories;

import com.company.web.forummanagementsystem.exceptions.EntityNotFoundException;
import com.company.web.forummanagementsystem.models.Comment;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.query.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class CommentRepositoryImpl implements CommentRepository {

    private final SessionFactory sessionFactory;

    public CommentRepositoryImpl(SessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
    }

    @Override
    public List<Comment> getAll() {
        try (Session session = sessionFactory.openSession()) {
            Query<Comment> query = session.createQuery("from Comment", Comment.class);
            return query.list();
        }
    }

    @Override
    public Comment getById(Long id) {
        try (Session session = sessionFactory.openSession()) {
            Comment comment = session.get(Comment.class, id);
            if (comment == null) throw new EntityNotFoundException("Comment", id);
            return comment;
        }
    }

    @Override
    public Comment create(Comment comment) {
        try (Session session = sessionFactory.openSession()) {
            session.beginTransaction();
            session.persist(comment);
            session.getTransaction().commit();
            return comment;
        }
    }

    @Override
    public Comment update(Comment comment) {
        try (Session session = sessionFactory.openSession()) {
            session.beginTransaction();
            session.merge(comment);
            session.getTransaction().commit();
            return comment;
        }
    }

    @Override
    public void delete(Long id) {
        try (Session session = sessionFactory.openSession()) {
            session.beginTransaction();
            Comment comment = session.get(Comment.class, id);
            session.remove(comment);
            session.getTransaction().commit();
        }
    }

    @Override
    public List<Comment> getCommentsByUserId(Long userId) {
        return null;
    }

    @Override
    public Comment getCommentByUserId(Long userId, Long commentId) {
        return null;
    }

    @Override
    public List<Comment> getCommentsByPostId(Long postId) {
        return null;
    }

    @Override
    public Comment getCommentByPostId(Long postId, Long commentId) {
        return null;
    }
}
