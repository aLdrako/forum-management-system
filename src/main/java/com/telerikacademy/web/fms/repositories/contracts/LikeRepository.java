package com.telerikacademy.web.fms.repositories.contracts;

import com.telerikacademy.web.fms.models.Like;

public interface LikeRepository {

    Like getById(Long postId, Long userId);
}
