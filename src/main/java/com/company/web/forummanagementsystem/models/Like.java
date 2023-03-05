package com.company.web.forummanagementsystem.models;

import jakarta.persistence.*;

import java.io.Serializable;
import java.util.Objects;


@Entity
@Table(name = "likes")
public class Like {
    @EmbeddedId
    private LikeId likeId;

    @ManyToOne
    @MapsId("id")
    private Post post;

    public Like() {}

    public Like(LikeId likeId, Post post) {
        this.likeId = likeId;
        this.post = post;
    }

    public LikeId getLikeId() {
        return likeId;
    }

    public void setLikeId(LikeId likeId) {
        this.likeId = likeId;
    }

    public Post getPost() {
        return post;
    }

    public void setPost(Post post) {
        this.post = post;
    }
}
