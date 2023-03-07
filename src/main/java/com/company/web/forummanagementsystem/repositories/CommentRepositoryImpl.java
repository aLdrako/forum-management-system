package com.company.web.forummanagementsystem.repositories;

import com.company.web.forummanagementsystem.exceptions.EntityNotFoundException;
import com.company.web.forummanagementsystem.models.Comment;
import com.company.web.forummanagementsystem.models.Post;
import com.company.web.forummanagementsystem.models.User;
import com.company.web.forummanagementsystem.repositories.contracts.CommentRepository;
import jakarta.persistence.criteria.*;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.query.Query;
import org.springframework.stereotype.Repository;

import java.util.*;
import java.util.concurrent.atomic.AtomicReference;

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
    public List<Comment> getCommentsByUserId(Long userId, Map<String, String> parameters) {
        Map<String, String> params = extractParams(parameters);
        try (Session session = sessionFactory.openSession()) {
            if (parameters.size() == 0) {
                Query<Comment> query = session.createQuery("from Comment where createdBy.id = :userId", Comment.class);
                query.setParameter("userId", userId);
                return query.list();
            } else {
                CriteriaBuilder builder = session.getCriteriaBuilder();
                CriteriaQuery<Comment> criteriaQuery = builder.createQuery(Comment.class);
                Root<Comment> root = criteriaQuery.from(Comment.class);
                Join<Comment, User> userJoin = root.join("createdBy");
                criteriaQuery.select(root).where(builder.equal(userJoin.get("id"), userId));
                return getComments(params, session, builder, criteriaQuery, root);
            }
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
    public List<Comment> getCommentsByPostId(Long postId, Map<String, String> parameters) {
        Map<String, String> params = extractParams(parameters);
        try (Session session = sessionFactory.openSession()) {
            if (parameters.size() == 0) {
                Query<Comment> query = session.createQuery("from Comment where postedOn.id = :postId", Comment.class);
                query.setParameter("postId", postId);
                return query.list();
            } else {
                CriteriaBuilder builder = session.getCriteriaBuilder();
                CriteriaQuery<Comment> criteriaQuery = builder.createQuery(Comment.class);
                Root<Comment> root = criteriaQuery.from(Comment.class);
                Join<Comment, Post> postJoin = root.join("postedOn");
                criteriaQuery.select(root).where(builder.equal(postJoin.get("id"), postId));
                return getComments(params, session, builder, criteriaQuery, root);
            }
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

    private static List<Comment> getComments(Map<String, String> params, Session session, CriteriaBuilder builder, CriteriaQuery<Comment> criteriaQuery, Root<Comment> commentRoot) {
        Path<Object> objectPath = commentRoot.get(params.get("sort"));
        Order order = params.get("order").equals("asc") ? builder.asc(objectPath) : builder.desc(objectPath);
        criteriaQuery.orderBy(order);
        return session.createQuery(criteriaQuery).getResultList();
    }

    private static Map<String, String> extractParams(Map<String, String> parameters) {
        AtomicReference<String> sortBy = new AtomicReference<>("dateCreated");
        AtomicReference<String> orderBy = new AtomicReference<>("asc");
        parameters.forEach((key, value) -> {
            if (key.contains("sort")) sortBy.set(value);
            if (key.contains("order")) orderBy.set(value);
        });
        Map<String, String> params = new HashMap<>();
        params.put("sort", String.valueOf(sortBy));
        params.put("order", String.valueOf(orderBy));
        return params;
    }
}
