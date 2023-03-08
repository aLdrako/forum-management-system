package com.telerikacademy.web.fms.models;

import jakarta.persistence.*;
import org.hibernate.annotations.Generated;
import org.hibernate.annotations.GenerationTime;
import org.hibernate.annotations.Where;

import java.time.LocalDateTime;
import java.util.*;

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

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
            name = "likes",
            joinColumns = @JoinColumn(name = "post_id"),
            inverseJoinColumns = @JoinColumn(name = "user_id")
    )
    private Set<User> likes;

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
            name = "posts_tags",
            joinColumns = @JoinColumn(name = "post_id"),
            inverseJoinColumns = @JoinColumn(name = "tag_id")
    )
    private Set<Tag> tags;

    @OneToMany(mappedBy = "postedOn", cascade = CascadeType.REMOVE, fetch = FetchType.EAGER)
    private Set<Comment> comments;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User userCreated;

    @Temporal(TemporalType.TIMESTAMP)
    @Generated(GenerationTime.ALWAYS)
    @Column(name = "date_created")
    private LocalDateTime dateCreated;

    public Post() {
        likes = new HashSet<>();
        tags = new HashSet<>();
    }

    public Post(Long id, String title, String content, User userCreated) {
        this.id = id;
        this.title = title;
        this.content = content;
        this.userCreated = userCreated;
        likes = new HashSet<>();
        tags = new HashSet<>();
    }
    public Post(Long id, String title, String content, User userCreated, LocalDateTime dateCreated) {
        this(id, title, content, userCreated);
        this.dateCreated = dateCreated;
        likes = new HashSet<>();
        tags = new HashSet<>();
    }

    public Set<Comment> getComments() {
        return new HashSet<>(comments);
    }

    public void setComments(Set<Comment> comments) {
        this.comments = comments;
    }

    public Set<Tag> getTags() {
        return new HashSet<>(tags);
    }
    public void addTag(Tag tag) {
        tags.add(tag);
    }
    public void removeTag(Tag tag) {
        tags.remove(tag);
    }

    public void setTags(Set<Tag> tags) {
        this.tags = tags;
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

    public Set<User> getLikes() {
        return new HashSet<>(likes);
    }

    public void removeLike(User user) {
        likes.remove(user);
    }

    public void addLike(User user) {
        likes.add(user);
    }

    public void setLikes(Set<User> likes) {
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
}
