package com.company.web.forummanagementsystem.models;

import jakarta.persistence.*;

import java.io.Serializable;
import java.util.Objects;


@Entity
@Table(name = "likes")
public class Like implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_post_id")
    private int id;

    @OneToOne
    @JoinColumn(name = "user_id", updatable = false)
    private User user;

    @OneToOne
    @JoinColumn(name = "post_id", updatable = false)
    private Post post;

    public Like() {
    }

    public Like(int id, User user, Post post) {
        this.id = id;
        this.user = user;
        this.post = post;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public Post getPost() {
        return post;
    }

    public void setPost(Post post) {
        this.post = post;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Like like = (Like) o;
        return Objects.equals(user, like.user) && Objects.equals(post, like.post);
    }

    @Override
    public int hashCode() {
        return Objects.hash(user, post);
    }
    //@Id
    //@OneToOne()
    //@JoinColumn(name = "user_id")
    //private Long userId;
    //@Id
    //@OneToOne
    //@JoinColumn(name = "post_id")
    //private Long postId;
//
    //public Like() {}
//
    //public Long getUser() {
    //    return userId;
    //}
//
    //public void setUser(Long user) {
    //    this.userId = user;
    //}
//
    //public Long getPost() {
    //    return postId;
    //}
//
    //public void setPost(Long post) {
    //    this.postId = post;
    //}
//
    //@Override
    //public boolean equals(Object o) {
    //    if (this == o) return true;
    //    if (o == null || getClass() != o.getClass()) return false;
    //    Like like = (Like) o;
    //    return Objects.equals(userId, like.userId) && Objects.equals(postId, like.postId);
    //}
//
    //@Override
    //public int hashCode() {
    //    return Objects.hash(userId, postId);
    //}
}
