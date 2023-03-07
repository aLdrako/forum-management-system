package com.company.web.forummanagementsystem.models.dto;

import com.company.web.forummanagementsystem.models.validations.CreateValidationGroup;
import com.company.web.forummanagementsystem.models.validations.UpdateValidationGroup;
import jakarta.validation.constraints.*;

public class CommentDTO {
    @NotEmpty(groups = {CreateValidationGroup.class, UpdateValidationGroup.class})
    @Size(min = 16, max = 1024, message = "Comment should be between 16 and 1024 symbols",
            groups = {CreateValidationGroup.class, UpdateValidationGroup.class})
    private String content;
    @NotNull(groups = {CreateValidationGroup.class})
    @Positive(message = "Post Id should be positive", groups = CreateValidationGroup.class)
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
