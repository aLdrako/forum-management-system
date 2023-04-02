package com.telerikacademy.web.fms.repositories;

import com.telerikacademy.web.fms.exceptions.EntityNotFoundException;
import com.telerikacademy.web.fms.models.Post;
import com.telerikacademy.web.fms.models.Tag;
import com.telerikacademy.web.fms.models.User;
import com.telerikacademy.web.fms.repositories.contracts.PostRepository;
import jakarta.persistence.TypedQuery;
import jakarta.persistence.criteria.*;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.query.Query;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static com.telerikacademy.web.fms.helpers.FilterAndSortParameters.extractFilterPredicate;
import static com.telerikacademy.web.fms.helpers.FilterAndSortParameters.extractSortOrderPosts;

@Repository
public class PostRepositoryImpl implements PostRepository {

    private final SessionFactory sessionFactory;

    @Autowired
    public PostRepositoryImpl(SessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
    }

    @Override
    public Post getById(Long id) {
        try (Session session = sessionFactory.openSession()) {
            Post post = session.get(Post.class, id);
            if (post == null) {
                throw new EntityNotFoundException("Post", id);
            }
            return post;
        }
    }

    @Override
    public List<Post> getAll(Map<String, String> parameters) {
        try (Session session = sessionFactory.openSession()) {
            CriteriaBuilder criteriaBuilder = session.getCriteriaBuilder();
            CriteriaQuery<Post> criteriaQuery = criteriaBuilder.createQuery(Post.class);
            Root<Post> post = criteriaQuery.from(Post.class);
            Join<Post, User> userJoin = post.join("userCreated");
            Join<Post, Tag> tagJoin = post.join("tags", JoinType.LEFT);
            Join<Post, User> likesJoin = post.join("likes", JoinType.LEFT);
            Map<String, String> sortOrderParams = extractSortOrderPosts(parameters);
            criteriaQuery.select(post).where(extractFilterPredicate(parameters, criteriaBuilder, post,
                    userJoin, tagJoin));

            criteriaQuery = extractOrder(sortOrderParams, criteriaBuilder, criteriaQuery, post, likesJoin,
                    userJoin);
            return session.createQuery(criteriaQuery).getResultList();
        }
    }

    private CriteriaQuery<Post> extractOrder(Map<String, String> sortOrderParams, CriteriaBuilder builder,
                                             CriteriaQuery<Post> criteriaQuery, Root<Post> postRoot, Join<Post, User> likesJoin,
                                             Join<Post, User> userJoin) {
        Path<Object> postPath = null;
        Order order = null;
        switch (sortOrderParams.get("sort").toLowerCase()) {
            case "likes" -> order = sortOrderParams.get("order").equalsIgnoreCase("desc") ?
                    builder.desc(builder.count(likesJoin)) : builder.asc(builder.count(likesJoin));
            case "userid" -> {
                postPath = userJoin.get("id");
                order = sortOrderParams.get("order").equalsIgnoreCase("desc") ?
                        builder.desc(postPath) : builder.asc(postPath);
            }
            default -> {
                postPath = postRoot.get(sortOrderParams.get("sort"));
                order = sortOrderParams.get("order").equalsIgnoreCase("desc") ? builder.desc(postPath) : builder.asc(postPath);
            }
        }
        return criteriaQuery.orderBy(order).groupBy(postRoot);
    }

    @Override
    public Post create(Post post) {
        try (Session session = sessionFactory.openSession()) {
            session.beginTransaction();
            session.persist(post);
            session.getTransaction().commit();
            return post;
        }
    }

    @Override
    public void update(Post post) {
        try (Session session = sessionFactory.openSession()) {
            session.beginTransaction();
            session.merge(post);
            session.getTransaction().commit();
        }
    }

    @Override
    public void delete(Post post) {
        try (Session session = sessionFactory.openSession()) {
            session.beginTransaction();
            session.remove(post);
            session.getTransaction().commit();
        }
    }

    @Override
    public List<Post> getTopTenMostCommented() {
        try (Session session = sessionFactory.openSession()) {
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
        try (Session session = sessionFactory.openSession()) {
            return session.createQuery("from Post p order by p.datecreated desc limit 10",
                    Post.class).list();
        }
    }

    @Override
    public Post getPostByUserId(Long userId, Long postId) {
        try (Session session = sessionFactory.openSession()) {
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
        try (Session session = sessionFactory.openSession()) {
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

    @Override
    public Page<Post> findAll(List<Post> allPosts, Pageable pageable, Map<String, String> parameters) {
        try (Session session = sessionFactory.openSession()) {
            if (parameters.size() == 0) {
                CriteriaQuery<Post> criteriaQuery = session.getCriteriaBuilder().createQuery(Post.class);
                Root<Post> root = criteriaQuery.from(Post.class);
                criteriaQuery.select(root);

                TypedQuery<Post> typedQuery = session.createQuery(criteriaQuery);
                typedQuery.setFirstResult(pageable.getPageNumber() * pageable.getPageSize());
                typedQuery.setMaxResults(pageable.getPageSize());
                List<Post> posts = typedQuery.getResultList();

                return new PageImpl<>(posts, pageable, getAll(Map.of()).size());
            } else {
                int pageNo = pageable.getPageNumber();
                int pageSize = pageable.getPageSize();
                int startIndex = pageNo * pageSize;
                List<Post> pageContent = new ArrayList<>();


                for (int i = startIndex; i < allPosts.size() && i < startIndex + pageSize; i++) {
                    pageContent.add(allPosts.get(i));
                }

                return new PageImpl<>(pageContent, PageRequest.of(pageNo, pageSize), allPosts.size());
            }
        }
    }
}
