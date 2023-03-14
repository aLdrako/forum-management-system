package com.telerikacademy.web.fms.models.dto;

import jakarta.validation.constraints.*;

import java.util.List;
import java.util.stream.Collectors;

public class PostDTO {
    @NotEmpty
    @Size(min = 16, max = 64, message = "Title should be between 16 and 64 symbols")
    private String title;
    @NotEmpty
    @Size(min = 32, max = 8192, message = "Content should be between 32 and 8192 symbols")
    private String content;

    private List<String> tags;

    public List<@Size(min = 5, max = 32,
            message = "Tags must not be between 5 and 32 symbols")String> getTags() {
        return tags.stream().map(tag -> tag.trim().replaceAll("\\s", ""))
                .collect(Collectors.toList());
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
