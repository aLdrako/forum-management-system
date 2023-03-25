package com.telerikacademy.web.fms.services.contracts;

import com.telerikacademy.web.fms.models.Post;
import com.telerikacademy.web.fms.models.User;

import java.util.List;
import java.util.Map;
import java.util.Optional;

public interface PostServices {
    Post getById(Long id);
    List<Post> getAll(Optional<Long> userId, Optional<String> title, Optional<String> content, Optional<String> tag,
                      Optional<String> sort, Optional<String> order);
    Post create(Post post, User user);
    void update(Post post, User user);
    void delete(Long id, User user);

    Post getPostByUserId(Long userId, Long postId);

    void changePostLikes(Long id, User user);

    void updateTagsInPost(List<String> tags, Post post);

    List<Post> search(Optional<String> keyword);
}
