package com.telerikacademy.web.fms.repositories;

import com.telerikacademy.web.fms.exceptions.EntityNotFoundException;
import com.telerikacademy.web.fms.models.Permission;
import com.telerikacademy.web.fms.models.User;
import com.telerikacademy.web.fms.repositories.contracts.UserRepository;
import jakarta.persistence.TypedQuery;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.Root;
import org.hibernate.Hibernate;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.query.Query;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

@Repository
public class UserRepositoryImpl implements UserRepository {

    private final SessionFactory sessionFactory;

    public UserRepositoryImpl(SessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
    }

    @Override
    public List<User> getAll() {
        try (Session session = sessionFactory.openSession()) {
            CriteriaQuery<User> query = getUserCriteriaQuery(session);
            return session.createQuery(query).getResultList();
        }
    }

    @Override
    public Page<User> findAll(Pageable pageable) {
        try (Session session = sessionFactory.openSession()) {
            CriteriaQuery<User> query = getUserCriteriaQuery(session);

            TypedQuery<User> typedQuery = session.createQuery(query);
            typedQuery.setFirstResult(pageable.getPageNumber() * pageable.getPageSize());
            typedQuery.setMaxResults(pageable.getPageSize());
            List<User> users = typedQuery.getResultList();

            return new PageImpl<>(users, pageable, getAll().size());
        }
    }

    private CriteriaQuery<User> getUserCriteriaQuery(Session session) {
        CriteriaBuilder builder = session.getCriteriaBuilder();
        CriteriaQuery<User> query = builder.createQuery(User.class);
        Root<User> root = query.from(User.class);
        Join<User, Permission> permissionJoin = root.join("permission");
        query.select(root).where(builder.equal(permissionJoin.get("isDeleted"), false));
        return query;
    }

    @Override
    public User getById(Long id) {
        try (Session session = sessionFactory.openSession()) {
            User user = session.get(User.class, id);
            Permission permission = session.get(Permission.class, id);
            if (user == null || permission.isDeleted()) throw new EntityNotFoundException("User", id);
            Hibernate.initialize(user.getComments());
            Hibernate.initialize(user.getPosts());
            return user;
        }
    }

    @Override
    public List<User> search(String parameter) {
        try (Session session = sessionFactory.openSession()) {
            String[] params = parameter.split("=", -1);
            String searchParams = "from User where email = ?1 OR username = ?1 OR firstName = ?1";
            Query<User> query = session.createQuery(searchParams, User.class);
            query.setParameter(1, !params[1].isBlank() ? params[1] : "*");
            List<User> list = query.list();
            if (list.size() == 0) throw new EntityNotFoundException("User", params[0], params[1]);
            return list;
        }
    }

    @Override
    public User create(User user) {
        try (Session session = sessionFactory.openSession()) {
            Permission permission = new Permission();
            session.beginTransaction();
            session.persist(user);
            permission.setUserId(user.getId());
            session.persist(permission);
            session.getTransaction().commit();
            user.setPermission(permission);
            return user;
        }
    }

    @Override
    public User update(User user) {
        try (Session session = sessionFactory.openSession()) {
            session.beginTransaction();
            session.merge(user);
            session.getTransaction().commit();
            return user;
        }
    }

    @Override
    public Permission updatePermissions(Permission permission) {
        try (Session session = sessionFactory.openSession()) {
            session.beginTransaction();
            session.merge(permission);
            session.getTransaction().commit();
            return permission;
        }
    }

    @Override
    public void delete(Long id) {
        try (Session session = sessionFactory.openSession()) {
            session.beginTransaction();
            Permission permission = session.get(Permission.class, id);
            permission.setDeleted(true);
            session.merge(permission);
            User user = session.get(User.class, id);
            user.setUsername(generateString());
            user.setPassword(generateString());
            user.setFirstName(generateString());
            user.setLastName(generateString());
            user.setEmail(generateString() + "@mail.com");
            user.setPhoneNumber(null);
            user.setPhoto(null);
            session.merge(user);
            session.getTransaction().commit();
        }
    }

    private String generateString() {
        return new Random().ints(48, 123)
                .filter(i -> (i <= 57 || i >= 65) && (i <= 90 || i >= 97)).limit(15)
                .collect(StringBuilder::new, StringBuilder::appendCodePoint, StringBuilder::append).toString();
    }
}
