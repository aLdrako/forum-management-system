package com.telerikacademy.web.fms.models.dto;

import java.util.List;

public class PostOutputDTO {
    private String title;
    private String content;
    private int likes;
    private List<String> tags;
    private String userCreated;
    private String dateCreated;

    public PostOutputDTO() {
    }

    public PostOutputDTO(String title, String content, String userCreated, int likes, List<String> tags, String dateCreated) {
        this.title = title;
        this.content = content;
        this.likes = likes;
        this.userCreated = userCreated;
        this.tags = tags;
        this.dateCreated = dateCreated;
    }

    public String getTitle() {
        return title;
    }

    public String getContent() {
        return content;
    }

    public int getLikes() {
        return likes;
    }

    public List<String> getTags() {
        return tags;
    }

    public String getUserCreated() {
        return userCreated;
    }

    public String getDateCreated() {
        return dateCreated;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public void setLikes(int likes) {
        this.likes = likes;
    }

    public void setTags(List<String> tags) {
        this.tags = tags;
    }

    public void setUserCreated(String userCreated) {
        this.userCreated = userCreated;
    }

    public void setDateCreated(String dateCreated) {
        this.dateCreated = dateCreated;
    }
}
