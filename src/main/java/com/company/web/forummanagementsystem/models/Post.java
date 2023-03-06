package com.company.web.forummanagementsystem.models;

import jakarta.persistence.*;
import org.hibernate.annotations.Generated;
import org.hibernate.annotations.GenerationTime;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Entity
@Table(name = "posts")
public class Post {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;
    @Column(name = "title")
    private String title;
    @Column(name = "content")
    private String content;

    @OneToMany(fetch = FetchType.EAGER, mappedBy = "post", cascade = CascadeType.REMOVE)
    private List<Like> likes;

    @OneToMany(fetch = FetchType.EAGER, mappedBy = "post", cascade = CascadeType.REMOVE)
    private List<PostTagRelation> tags;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User userCreated;

    @Temporal(TemporalType.TIMESTAMP)
    @Generated(GenerationTime.ALWAYS)
    @Column(name = "date_created")
    private LocalDateTime dateCreated;

    public Post() {
        likes = new ArrayList<>();
        tags = new ArrayList<>();
    }

    public Post(Long id, String title, String content, User userCreated) {
        this.id = id;
        this.title = title;
        this.content = content;
        this.userCreated = userCreated;
        likes = new ArrayList<>();
        tags = new ArrayList<>();
    }
    public Post(Long id, String title, String content, User userCreated, LocalDateTime dateCreated) {
        this(id, title, content, userCreated);
        this.dateCreated = dateCreated;
        likes = new ArrayList<>();
        tags = new ArrayList<>();
    }

    public List<PostTagRelation> getTags() {
        return tags;
    }

    public void setTags(List<PostTagRelation> tags) {
        this.tags = tags;
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

    public List<Like> getLikes() {
        return likes;
    }

    public void setLikes(List<Like> likes) {
        this.likes = likes;
    }

    public User getUserCreated() {
        return userCreated;
    }

    public void setUserCreated(User userCreated) {
        this.userCreated = userCreated;
    }

    public LocalDateTime getDateCreated() {
        return dateCreated;
    }

    public void setDateCreated(LocalDateTime dateCreated) {
        this.dateCreated = dateCreated;
    }
}
