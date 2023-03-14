package com.telerikacademy.web.fms.repositories;

import com.telerikacademy.web.fms.exceptions.EntityNotFoundException;
import com.telerikacademy.web.fms.models.Post;
import com.telerikacademy.web.fms.models.Tag;
import com.telerikacademy.web.fms.models.User;
import com.telerikacademy.web.fms.repositories.contracts.PostRepository;
import jakarta.persistence.criteria.*;
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
    public List<Post> getAll(Optional<Long> userId, Optional<String> title, Optional<String> content,
                             Optional<String> sort, Optional<String> order) {
        try (Session session = sessionFactory.openSession()){
            StringBuilder stringQuery = new StringBuilder("from Post p left join p.likes as pl");
            Map<String, Object> queryParams = new HashMap<>();
            List<String> filter = new ArrayList<>();

            if (userId.isPresent()) {
                filter.add(" p.userCreated.id = :userId ");
                queryParams.put("userId", userId.get());
            }
            if (title.isPresent()) {
                filter.add(" p.title like :title ");
                queryParams.put("title", "%" + title.get() + "%");
            }
            if (content.isPresent()) {
                filter.add(" p.content like :content ");
                queryParams.put("content", "%" + content.get() + "%");
            }
            if (!filter.isEmpty()) {
                stringQuery.append(" where ").append(String.join(" and ", filter));
            }

            stringQuery.append(sort.map(this::generateSort).orElse(" order by p.id "));
            stringQuery.append(order.map(this::generateOrder).orElse(""));

            Query<Post> query = session.createQuery(stringQuery.toString(), Post.class);
            query.setProperties(queryParams);
            return query.list();
        }
    }

    private String generateOrder(String order) {
        if (order.equalsIgnoreCase("desc")) {
            return " desc ";
        }
        return "";
    }

    private String generateSort(String sort) {
        switch (sort.toLowerCase()) {
            case "title" -> {
                return "  order by p.title ";
            }
            case "userid" -> {
                return "  order by p.userCreated.id ";
            }
            case "datecreated" -> {
                return "  order by p.dateCreated ";
            }
            case "likes" -> {
                return " group by p order by count(pl) ";
            }
        }
        return "";
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
    public void delete(Post post) {
        try (Session session = sessionFactory.openSession()){
            session.beginTransaction();
            session.remove(post);
            session.getTransaction().commit();
        }
    }

    @Override
    public List<Post> getTopTenMostCommented() {
        try (Session session = sessionFactory.openSession()){
            return session.createQuery("""
                                        from Post p
                                        left join p.comments as pc
                                        group by p
                                        order by count(pc) desc
                                        limit 10
                                        """, Post.class).list();
        }
    }

    @Override
    public List<Post> getTopTenMostRecent() {
        try (Session session = sessionFactory.openSession()){
            return session.createQuery("from Post p order by p.dateCreated desc limit 10",
                    Post.class).list();
        }
    }

    @Override
    public Post getPostByUserId(Long userId, Long postId) {
        try (Session session = sessionFactory.openSession()){
            Query<Post> query = session.createQuery("from Post where id = :postId and " +
                    "userCreated.id = :userId", Post.class);
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
    public List<Post> search(Optional<String> keyword) {
        String query = keyword.orElse("");
        try (Session session = sessionFactory.openSession()){
            CriteriaBuilder criteriaBuilder = session.getCriteriaBuilder();
            CriteriaQuery<Post> criteriaQuery = criteriaBuilder.createQuery(Post.class);
            Root<Post> post = criteriaQuery.from(Post.class);
            Join<Post, User> userJoin = post.join("userCreated");
            Join<Post, Tag> tagJoin = post.join("tags", JoinType.LEFT);
            Predicate tagPredicate = criteriaBuilder.equal(tagJoin.get("name"), query);
            Predicate titlePredicate = criteriaBuilder.like(post.get("title"), "%" + query + "%");
            Predicate contentPredicate = criteriaBuilder.like(post.get("content"), "%" + query + "%");
            Predicate userPredicate = criteriaBuilder.like(userJoin.get("username"), "%" + query + "%");

            Predicate finalPredicate = criteriaBuilder.or(tagPredicate, titlePredicate, contentPredicate,
                    userPredicate
            );
            criteriaQuery.where(finalPredicate);
            return session.createQuery(criteriaQuery).getResultList();
        }
    }
}
