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

    private static final String USER_NO_COMMENT_WITH_ID = "User with id %d does not have comment with id %d!";
    private static final String POST_NO_COMMENT_WITH_ID = "Post with id %d does not have comment with id %d!";
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
        try (Session session = sessionFactory.openSession()) {
            Query<Comment> query = session.createQuery("from Comment where createdBy.id = :userId", Comment.class);
            query.setParameter("userId", userId);
            return query.list();
        }
    }

    @Override
    public Comment getCommentByUserId(Long userId, Long commentId) {
        try (Session session = sessionFactory.openSession()) {
            Query<Comment> query = session.createQuery("from Comment where id = :commentId and createdBy.id = :userId", Comment.class);
            query.setParameter("commentId", commentId);
            query.setParameter("userId", userId);
            List<Comment> list = query.list();
            if (list.size() == 0) throw new EntityNotFoundException(String.format(USER_NO_COMMENT_WITH_ID, userId, commentId));
            return list.get(0);
        }
    }

    @Override
    public List<Comment> getCommentsByPostId(Long postId) {
        try (Session session = sessionFactory.openSession()) {
            Query<Comment> query = session.createQuery("from Comment where postedOn.id = :postId", Comment.class);
            query.setParameter("postId", postId);
            return query.list();
        }
    }

    @Override
    public Comment getCommentByPostId(Long postId, Long commentId) {
        try (Session session = sessionFactory.openSession()) {
            Query<Comment> query = session.createQuery("from Comment where id = :commentId and postedOn.id = :postId", Comment.class);
            query.setParameter("commentId", commentId);
            query.setParameter("postId", postId);
            List<Comment> list = query.list();
            if (list.size() == 0) throw new EntityNotFoundException(String.format(POST_NO_COMMENT_WITH_ID, postId, commentId));
            return list.get(0);
        }
    }
}
