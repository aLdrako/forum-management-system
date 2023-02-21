package com.company.web.forummanagementsystem.models;

import jakarta.validation.constraints.*;

public class PostDTO {
    @NotEmpty
    @Size(min = 16, max = 64, message = "Title should be between 4 and 32 symbols")
    private String title;
    @NotEmpty
    @Size(min = 32, max = 8192, message = "Content should be between 4 and 32 symbols")
    private String content;
    @Min(value = 0, message = "The value must be positive or zero")
    private int likes;
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

    public int getLikes() {
        return likes;
    }

    public void setLikes(int likes) {
        this.likes = likes;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }
}
