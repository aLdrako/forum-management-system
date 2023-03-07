package com.company.web.forummanagementsystem.repositories.contracts;

import com.company.web.forummanagementsystem.models.Like;

public interface LikeRepository {

    Like getById(Long postId, Long userId);
}
