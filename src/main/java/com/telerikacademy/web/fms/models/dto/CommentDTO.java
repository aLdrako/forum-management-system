package com.telerikacademy.web.fms.models.dto;

import com.telerikacademy.web.fms.models.validations.CreateValidationGroup;
import com.telerikacademy.web.fms.models.validations.UpdateValidationGroup;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

public class CommentDTO {
    @NotEmpty(message = "Content can't be empty", groups = {CreateValidationGroup.class, UpdateValidationGroup.class})
    @Size(min = 16, max = 1024, message = "Comment should be between 16 and 1024 symbols",
            groups = {CreateValidationGroup.class, UpdateValidationGroup.class})
    private String content;
    @NotNull(message = "Post Id can't be null", groups = {UpdateValidationGroup.class})
    @Positive(message = "Post Id should be positive", groups = UpdateValidationGroup.class)
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
