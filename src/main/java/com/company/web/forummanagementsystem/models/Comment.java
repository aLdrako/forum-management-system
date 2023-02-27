package com.company.web.forummanagementsystem.models;

import jakarta.validation.constraints.*;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Objects;
import static com.company.web.forummanagementsystem.helpers.DateTimeFormat.*;

public class Comment {
    private Long id;
    @NotEmpty
    @Size(min = 16, max = 1024, message = "Comment should be between 16 and 1024 symbols")
    private String content;
    @NotNull
    @Positive(message = "Post Id should be positive")
    private Long postId;

    private Long userId;
    private LocalDateTime dateCreated;

    public Comment() {
    }

    public Comment(Long id, String content, Long postId, Long userId) {
        this.id = id;
        this.content = content;
        this.postId = postId;
        this.userId = userId;
    }

    public Comment(Long id, String content, Long postId, Long userId, LocalDateTime dateCreated) {
        this.id = id;
        this.content = content;
        this.postId = postId;
        this.userId = userId;
        this.dateCreated = dateCreated;
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
    public Long getUserId() {
        return userId;
    }

    public String getDateCreated() {
        return formatToString(dateCreated);
    }
    public void setPostId(Long postId) {
        this.postId = postId;
    }
    public void setDateCreated(LocalDateTime dateCreated) {
        this.dateCreated = dateCreated;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
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
