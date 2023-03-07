package com.telerikacademy.web.fms.repositories;

import com.telerikacademy.web.fms.exceptions.EntityNotFoundException;
import com.telerikacademy.web.fms.models.Like;
import com.telerikacademy.web.fms.models.Post;
import com.telerikacademy.web.fms.repositories.contracts.PostRepository;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.query.Query;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.*;

@Repository
public class PostRepositoryImpl implements PostRepository {

    private final SessionFactory sessionFactory;
    @Autowired
    public PostRepositoryImpl(SessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
    }

    @Override
    public Post getById(Long id) {
        try (Session session = sessionFactory.openSession()){
            Post post = session.get(Post.class, id);
            if (post == null) {
                throw new EntityNotFoundException("Post", id);
            }
            return post;
        }
    }

    @Override
    public List<Post> getAll(Optional<Long> userId, Optional<String> title, Optional<String> sortBy, Optional<String> orderBy) {
        try (Session session = sessionFactory.openSession()){
            StringBuilder stringQuery = new StringBuilder("from Post");
            Map<String, Object> queryParams = new HashMap<>();
            List<String> filter = new ArrayList<>();

            if (userId.isPresent()) {
                filter.add(" userCreated.id = :userId ");
                queryParams.put("userId", userId.get());
            }
            if (title.isPresent()) {
                filter.add(" title like :title ");
                queryParams.put("title", "%" + title.get() + "%");
            }
            if (!filter.isEmpty()) {
                stringQuery.append(" where ").append(String.join(" and ", filter));
            }
            stringQuery.append(sortBy.map(this::generateSort).orElse(" order by id "));
            stringQuery.append(orderBy.map(this::generateOrder).orElse(""));

            Query<Post> query = session.createQuery(stringQuery.toString(), Post.class);
            query.setProperties(queryParams);
            return query.list();
        }
    }

    private String generateOrder(String orderBy) {
        if (orderBy.equalsIgnoreCase("desc")) {
            return " desc ";
        }
        return "";
    }

    private String generateSort(String sortBy) {
        switch (sortBy.toLowerCase()) {
            case "title":
                return " order by title ";
            case "userid":
                return " order by userCreated.id ";
            case "datecreated":
                return " order by dateCreated ";
            default:
                return "";
        }
    }

    @Override
    public Post create(Post post) {
        try (Session session = sessionFactory.openSession()){
            session.beginTransaction();
            session.persist(post);
            session.getTransaction().commit();
            return post;
        }
    }

    @Override
    public Post update(Post post) {
        try (Session session = sessionFactory.openSession()){
            session.beginTransaction();
            session.merge(post);
            session.getTransaction().commit();
        }
        return post;
    }

    @Override
    public void delete(Long id) {
        Post postToRemove = getById(id);
        try (Session session = sessionFactory.openSession()){
            session.beginTransaction();
            session.remove(postToRemove);
            session.getTransaction().commit();
        }
    }

    @Override
    public List<Post> getTopTenMostCommented() {
        return null;
    }

    @Override
    public List<Post> getTopTenMostRecent() {
        return null;
    }

    @Override
    public Post getPostByUserId(Long userId, Long postId) {
        try (Session session = sessionFactory.openSession()){
            Query<Post> query = session.createQuery("from Post where id = :postId and userCreated.id = :userId",
                    Post.class);
            query.setParameter("postId", postId);
            query.setParameter("userId", userId);
            List<Post> result = query.list();
            if (result.size() == 0) {
                throw new EntityNotFoundException("Post", postId, "user id", userId);
            }
            return result.get(0);
        }
    }

    @Override
    public void addLikeToPost(Like like) {
        try (Session session = sessionFactory.openSession()){
            session.beginTransaction();
            session.persist(like);
            session.getTransaction().commit();
        }
    }

    @Override
    public void removeLikeFromPost(Like like) {
        try (Session session = sessionFactory.openSession()){
            session.beginTransaction();
            session.remove(like);
            session.getTransaction().commit();
        }
    }
}
