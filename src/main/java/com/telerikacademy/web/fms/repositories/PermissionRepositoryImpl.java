package com.company.web.forummanagementsystem.repositories;

import com.company.web.forummanagementsystem.exceptions.EntityNotFoundException;
import com.company.web.forummanagementsystem.models.Permission;
import com.company.web.forummanagementsystem.repositories.contracts.PermissionRepository;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.springframework.stereotype.Repository;

@Repository
public class PermissionRepositoryImpl implements PermissionRepository {

    private final SessionFactory sessionFactory;

    public PermissionRepositoryImpl(SessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
    }

    @Override
    public Permission getById(Long id) {
        try (Session session = sessionFactory.openSession()) {
            Permission permission = session.get(Permission.class, id);
            if (permission == null || permission.isDeleted()) throw new EntityNotFoundException("User", id);
            return permission;
        }
    }

    public Permission update(Permission permission) {
        try (Session session = sessionFactory.openSession()) {
            session.beginTransaction();
            session.merge(permission);
            session.getTransaction().commit();
            return permission;
        }
    }
}
