package com.company.web.forummanagementsystem.models;

import jakarta.validation.constraints.*;

public class PostDTO {
    @NotEmpty
    @Size(min = 16, max = 64, message = "Title should be between 16 and 64 symbols")
    private String title;
    @NotEmpty
    @Size(min = 32, max = 8192, message = "Content should be between 32 and 8192 symbols")
    private String content;
    @NotNull
    @Positive(message = "User Id should be positive")
    private Long userId;

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

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
}
