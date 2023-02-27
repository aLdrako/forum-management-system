package com.company.web.forummanagementsystem.models;

import java.time.LocalDateTime;
import java.util.Objects;
import static com.company.web.forummanagementsystem.helpers.DateTimeFormat.*;

public class Post {

    private Long id;

    private String title;

    private String content;

    private int likes;

    private Long userId;

    private LocalDateTime dateCreated;

    public Post() {
    }

    public Post(Long id, String title, String content, int likes, Long userId) {
        this.id = id;
        this.title = title;
        this.content = content;
        this.likes = likes;
        this.userId = userId;
    }
    public Post(Long id, String title, String content, int likes, Long userId, LocalDateTime dateCreated) {
        this(id, title, content, likes, userId);
        this.dateCreated = dateCreated;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Post post = (Post) o;
        return Objects.equals(id, post.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
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

    public int getLikes() {
        return likes;
    }

    public void setLikes(int likes) {
        this.likes = likes;
    }

    public Long getUserId() {
        return userId;
    }
    public String getDateCreated() {
        return formatToString(dateCreated);
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public void setDateCreated(LocalDateTime dateCreated) {
        this.dateCreated = dateCreated;
    }
}
