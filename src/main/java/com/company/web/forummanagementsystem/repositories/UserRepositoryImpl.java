package com.company.web.forummanagementsystem.repositories;

import com.company.web.forummanagementsystem.exceptions.EntityNotFoundException;
import com.company.web.forummanagementsystem.models.Permission;
import com.company.web.forummanagementsystem.models.User;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.query.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class UserRepositoryImpl implements UserRepository {

    private final SessionFactory sessionFactory;

    public UserRepositoryImpl(SessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
    }

    @Override
    public List<User> getAll() {
        try (Session session = sessionFactory.openSession()) {
            Query<User> query = session.createQuery("from User", User.class);
            return query.list();
        }
    }

    @Override
    public User getById(Long id) {
        try (Session session = sessionFactory.openSession()) {
            User user = session.get(User.class, id);
            if (user == null) throw new EntityNotFoundException("User", id);
            return user;
        }
    }

    @Override
    public List<User> search(String parameter) {
        return null;
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
        return null;
    }

    @Override
    public void delete(Long id) {

    }
}
