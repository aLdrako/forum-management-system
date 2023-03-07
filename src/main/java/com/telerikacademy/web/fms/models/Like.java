package com.company.web.forummanagementsystem.models;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;

@Entity
@Table(name = "likes")
public class Like {
    @EmbeddedId
    private LikeId likeId;
    @JsonIgnore
    @ManyToOne
    @MapsId("id")
    private Post post;

    public Like() {}

    public Like(LikeId likeId) {
        this.likeId = likeId;
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
