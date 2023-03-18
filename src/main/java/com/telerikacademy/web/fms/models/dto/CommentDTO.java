package com.telerikacademy.web.fms.models.dto;

import com.telerikacademy.web.fms.models.validations.RegisterValidationGroup;
import com.telerikacademy.web.fms.models.validations.UpdateValidationGroup;
import jakarta.validation.constraints.*;

public class CommentDTO {
    @NotEmpty(message = "Content can't be empty", groups = {RegisterValidationGroup.class, UpdateValidationGroup.class})
    @Size(min = 16, max = 1024, message = "Comment should be between 16 and 1024 symbols",
            groups = {RegisterValidationGroup.class, UpdateValidationGroup.class})
    private String content;
    @NotNull(message = "Post Id can't be null", groups = {RegisterValidationGroup.class})
    @Positive(message = "Post Id should be positive", groups = RegisterValidationGroup.class)
    private Long postId;

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
}
