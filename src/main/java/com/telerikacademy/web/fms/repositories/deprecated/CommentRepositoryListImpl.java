package com.telerikacademy.web.fms.repositories.deprecated;

import com.telerikacademy.web.fms.exceptions.EntityNotFoundException;
import com.telerikacademy.web.fms.models.Comment;
import com.telerikacademy.web.fms.models.Post;
import com.telerikacademy.web.fms.models.User;
import com.telerikacademy.web.fms.repositories.contracts.CommentRepository;
import com.telerikacademy.web.fms.repositories.contracts.PostRepository;
import com.telerikacademy.web.fms.repositories.contracts.UserRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Collectors;

//@Repository
public class CommentRepositoryListImpl implements CommentRepository {
    private final List<Comment> comments;
    private final UserRepository userRepository;
    private final PostRepository postRepository;

    public CommentRepositoryListImpl(UserRepository userRepository, PostRepository postRepository) {
        this.userRepository = userRepository;
        this.postRepository = postRepository;
        List<User> users = this.userRepository.getAll();
//        List<Post> posts = this.postRepository.getAll("parameter", "sortBy", "orderBy");
        comments = new ArrayList<>();

        AtomicLong counter = new AtomicLong();
        //comments.add(new Comment(counter.incrementAndGet(), "This is my first comment", posts.get(0).getId(), users.get(1).getId()));
        //comments.add(new Comment(counter.incrementAndGet(), "Great idea, thanks", posts.get(1).getId(), users.get(1).getId()));
        //comments.add(new Comment(counter.incrementAndGet(), "Some random comment here", posts.get(1).getId(), users.get(2).getId()));
    }

    @Override
    public List<Comment> getAll() {
        return new ArrayList<>(comments);
    }

    @Override
    public Comment getById(Long id) {
        return comments.stream()
                .filter(comment -> Objects.equals(comment.getId(), id))
                .findFirst()
                .orElseThrow(() -> new EntityNotFoundException("Comment", id));
    }

    @Override
    public Comment create(Comment comment) {
        long currentId = !comments.isEmpty() ? comments.get(comments.size() - 1).getId() + 1 : 1L;
        comment.setId(currentId);
        userRepository.getById(comment.getCreatedBy().getId());
        postRepository.getById(comment.getPostedOn().getId());
        comments.add(comment);
        return comment;
    }

    @Override
    public Comment update(Comment comment) {
        Comment commentToUpdate = getById(comment.getId());
        User user = userRepository.getById(comment.getCreatedBy().getId());
        Post post = postRepository.getById(comment.getPostedOn().getId());
        commentToUpdate.setContent(comment.getContent());
        commentToUpdate.setCreatedBy(user);
        commentToUpdate.setPostedOn(post);
        return commentToUpdate;
    }

    @Override
    public void delete(Long id) {
        Comment commentToDelete = getById(id);
        comments.remove(commentToDelete);
    }
}
