package com.telerikacademy.web.fms.models.dto;

import jakarta.validation.constraints.*;

import java.util.List;
import java.util.stream.Collectors;

public class PostDTO {
    @NotEmpty
    @Size(min = 16, max = 64, message = "Title should be between 16 and 64 symbols!")
    private String title;
    @NotEmpty
    @Size(min = 32, max = 8192, message = "Content should be between 32 and 8192 symbols!")
    private String content;

    private List<String> tags;

    public List<@Pattern(regexp = "^[A-Za-z0-9]{5,32}$",
            message ="Tags must be between 5 and 32 characters and no special symbols!")String> getTags() {
        return tags;
    }

    public void setTags(List<String> tags) {
        this.tags = tags;
    }

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
}
