package com.company.web.forummanagementsystem.models;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

public class CommentDTO {
    @NotEmpty
    @Size(min = 16, max = 1024, message = "Comment should be between 16 and 1024 symbols")
    private String content;
    @NotNull
    @Positive(message = "User Id should be positive")
    private Long userId;
    @NotNull
    @Positive(message = "Post Id should be positive")
    private Long postId;

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public Long getPostId() {
        return postId;
    }

    public void setPostId(Long postId) {
        this.postId = postId;
    }
}
