package com.company.web.forummanagementsystem.repositories;

import com.company.web.forummanagementsystem.models.Like;
import com.company.web.forummanagementsystem.models.LikeId;

import java.util.List;

public interface LikeRepository {

    Like getById(Long postId, Long userId);
}
