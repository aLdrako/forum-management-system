package com.telerikacademy.web.fms.service.contracts;

import com.telerikacademy.web.fms.models.Post;
import com.telerikacademy.web.fms.models.User;

import java.util.List;
import java.util.Optional;

public interface PostServices {
    Post getById(Long id);
    List<Post> getAll(Optional<Long> userId, Optional<String> title,
                      Optional<String> sortBy, Optional<String> orderBy);
    Post create(Post post, User user);
    Post update(Post post, User user);
    void delete(Long id, User user);

    Post getPostByUserId(Long userId, Long postId);

    void changePostLikes(Long id, User user);
}
