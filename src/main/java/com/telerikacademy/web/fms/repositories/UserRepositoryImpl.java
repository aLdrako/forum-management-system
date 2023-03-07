package com.company.web.forummanagementsystem.repositories;

import com.company.web.forummanagementsystem.exceptions.EntityNotFoundException;
import com.company.web.forummanagementsystem.models.Permission;
import com.company.web.forummanagementsystem.models.User;
import com.company.web.forummanagementsystem.repositories.contracts.UserRepository;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.Root;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.query.Query;
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
            CriteriaBuilder builder = session.getCriteriaBuilder();
            CriteriaQuery<User> query = builder.createQuery(User.class);
            Root<User> root = query.from(User.class);
            Join<User, Permission> permissionJoin = root.join("permission");
            query.select(root).where(builder.equal(permissionJoin.get("isDeleted"), false));
            return session.createQuery(query).getResultList();
        }
    }

    @Override
    public User getById(Long id) {
        try (Session session = sessionFactory.openSession()) {
            User user = session.get(User.class, id);
            Permission permission = session.get(Permission.class, id);
            if (user == null || permission.isDeleted()) throw new EntityNotFoundException("User", id);
            return user;
        }
    }

    @Override
    public List<User> search(String parameter) {
        try (Session session = sessionFactory.openSession()) {
            String[] params = parameter.split("=");
            boolean hasParams = true;
            String searchParams = switch (params[0]) {
                case "email" -> "where email = ?1";
                case "username" -> "where username = ?1";
                case "firstName" -> "where firstName = ?1";
                default -> { hasParams = false; yield ""; }
            };
            if (hasParams) {
                Query<User> query = session.createQuery("from User " + searchParams, User.class);
                query.setParameter(1, params[1]);
                List<User> list = query.list();
                if (list.size() == 0) throw new EntityNotFoundException("User", params[0], params[1]);
                return list;
            } else return new ArrayList<>();
        }
    }

    @Override
    public User create(User user) {
        try (Session session = sessionFactory.openSession()) {
            Permission permission = new Permission();
            session.beginTransaction();
            session.persist(user);
            permission.setUser_id(user.getId());
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
    public void delete(Long id) {
        try (Session session = sessionFactory.openSession()) {
            session.beginTransaction();
            Permission permission = session.get(Permission.class, id);
            permission.setDeleted(true);
            session.merge(permission);
            User user = session.get(User.class, id);
            user.setUsername(generateString());
            user.setFirstName(generateString());
            user.setLastName(generateString());
            user.setEmail(generateString() + "@mail.com");
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