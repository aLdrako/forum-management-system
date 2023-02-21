package com.company.web.forummanagementsystem.models;

import jakarta.validation.constraints.*;

import java.time.LocalDateTime;
import java.util.Objects;

public class Comment {
    private Long id;
    @NotEmpty
    @Size(min = 16, max = 1024, message = "Comment should be between 16 and 1024 symbols")
    private String content;
    @NotNull
    @Positive(message = "Post Id should be positive")
    private Long postId;
    @NotNull
    @Positive(message = "User Id should be positive")
    private Long userId;
    private final LocalDateTime dateCreated = LocalDateTime.now();

    public Comment() {
    }

    public Comment(Long id, String content, Long postId, Long userId) {
        this.id = id;
        this.content = content;
        this.postId = postId;
        this.userId = userId;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public Long getPostId() {
        return postId;
    }

    public void setPostId(Long postId) {
        this.postId = postId;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public LocalDateTime getDateCreated() {
        return dateCreated;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Comment comment = (Comment) o;
        return Objects.equals(id, comment.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
