package com.company.web.forummanagementsystem.repositories;

import com.company.web.forummanagementsystem.models.Like;
import com.company.web.forummanagementsystem.models.LikeId;
import com.company.web.forummanagementsystem.repositories.contracts.LikeRepository;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

@Repository
public class LikeRepositoryImpl implements LikeRepository {

    private final SessionFactory sessionFactory;
    @Autowired
    public LikeRepositoryImpl(SessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
    }

    @Override
    public Like getById(Long postId, Long userId) {
        LikeId id = new LikeId(postId, userId);
        try (Session session = sessionFactory.openSession()){
            return session.get(Like.class, id);
        }
    }
}
