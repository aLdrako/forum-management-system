package com.company.web.forummanagementsystem.models;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.io.Serializable;
import java.util.Objects;
@Embeddable
public class LikeId implements Serializable {
    @Column(name = "post_id")
    private Long postId;
    @Column(name = "user_id")
    private Long userIdd;

    public LikeId() {
    }

    public LikeId(Long postId, Long userIdd) {
        this.postId = postId;
        this.userIdd = userIdd;
    }

    public Long getPostId() {
        return postId;
    }

    public void setPostId(Long postId) {
        this.postId = postId;
    }

    public Long getUserIdd() {
        return userIdd;
    }

    public void setUserIdd(Long userIdd) {
        this.userIdd = userIdd;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        LikeId likeId = (LikeId) o;
        return Objects.equals(postId, likeId.postId) && Objects.equals(userIdd, likeId.userIdd);
    }

    @Override
    public int hashCode() {
        return Objects.hash(postId, userIdd);
    }
}
