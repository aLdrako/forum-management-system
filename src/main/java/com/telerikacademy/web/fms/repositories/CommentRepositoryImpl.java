package com.telerikacademy.web.fms.repositories;

import com.telerikacademy.web.fms.exceptions.EntityNotFoundException;
import com.telerikacademy.web.fms.models.Comment;
import com.telerikacademy.web.fms.models.Post;
import com.telerikacademy.web.fms.models.User;
import com.telerikacademy.web.fms.repositories.contracts.CommentRepository;
import jakarta.persistence.TypedQuery;
import jakarta.persistence.criteria.*;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.query.Query;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static com.telerikacademy.web.fms.helpers.FilterAndSortParameters.extractSortOrderComments;

@Repository
public class CommentRepositoryImpl implements CommentRepository {
    private static final String USER_NO_COMMENT_WITH_ID = "User with id %d does not have comment with id %d!";
    private static final String POST_NO_COMMENT_WITH_ID = "Post with id %d does not have comment with id %d!";

    private final SessionFactory sessionFactory;

    public CommentRepositoryImpl(SessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
    }

    @Override
    public List<Comment> getAll(Map<String, String> parameters) {
        try (Session session = sessionFactory.openSession()) {
            if (parameters.size() == 0) {
                Query<Comment> query = session.createQuery("from Comment", Comment.class);
                return query.list();
            } else {
                CriteriaBuilder builder = session.getCriteriaBuilder();
                CriteriaQuery<Comment> criteriaQuery = builder.createQuery(Comment.class);
                Root<Comment> root = criteriaQuery.from(Comment.class);
                return getComments(parameters, session, builder, criteriaQuery, root);
            }
        }
    }

    @Override
    public Page<Comment> findAll(Map<String, String> parameters, Pageable pageable) {
        try (Session session = sessionFactory.openSession()) {
            if (parameters.size() == 0) {
                CriteriaQuery<Comment> criteriaQuery = session.getCriteriaBuilder().createQuery(Comment.class);
                Root<Comment> root = criteriaQuery.from(Comment.class);
                criteriaQuery.select(root);

                TypedQuery<Comment> typedQuery = session.createQuery(criteriaQuery);
                typedQuery.setFirstResult(pageable.getPageNumber() * pageable.getPageSize());
                typedQuery.setMaxResults(pageable.getPageSize());
                List<Comment> comments = typedQuery.getResultList();

                return new PageImpl<>(comments, pageable, getAll(Map.of()).size());
            } else {
                int pageNo = pageable.getPageNumber();
                int pageSize = pageable.getPageSize();
                int startIndex = pageNo * pageSize;
                List<Comment> pageContent = new ArrayList<>();

                CriteriaBuilder builder = session.getCriteriaBuilder();
                CriteriaQuery<Comment> criteriaQuery = builder.createQuery(Comment.class);
                Root<Comment> root = criteriaQuery.from(Comment.class);
                List<Comment> comments = getComments(parameters, session, builder, criteriaQuery, root);

                for (int i = startIndex; i < comments.size() && i < startIndex + pageSize; i++) {
                    pageContent.add(comments.get(i));
                }

                return new PageImpl<>(pageContent, PageRequest.of(pageNo, pageSize), comments.size());
            }
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
                return getComments(parameters, session, builder, criteriaQuery, root);
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
            if (list.size() == 0)
                throw new EntityNotFoundException(String.format(USER_NO_COMMENT_WITH_ID, userId, commentId));
            return list.get(0);
        }
    }

    @Override
    public List<Comment> getCommentsByPostId(Long postId, Map<String, String> parameters) {
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
                return getComments(parameters, session, builder, criteriaQuery, root);
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
            if (list.size() == 0)
                throw new EntityNotFoundException(String.format(POST_NO_COMMENT_WITH_ID, postId, commentId));
            return list.get(0);
        }
    }

    private static List<Comment> getComments(Map<String, String> parameters, Session session, CriteriaBuilder builder, CriteriaQuery<Comment> criteriaQuery, Root<Comment> root) {
        Map<String, String> params = extractSortOrderComments(parameters);
        Path<Object> commentFieldPath = root.get(params.get("sort"));
        Order order = params.get("order").equalsIgnoreCase("desc") ? builder.desc(commentFieldPath) : builder.asc(commentFieldPath);
        criteriaQuery.orderBy(order);
        return session.createQuery(criteriaQuery).getResultList();
    }
}
