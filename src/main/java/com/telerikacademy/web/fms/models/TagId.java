package com.telerikacademy.web.fms.models;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;

import java.io.Serializable;
import java.util.Objects;

@Embeddable
public class TagId implements Serializable {
    @JsonIgnore
    @Column(name = "post_id")
    private Long postId;
    @ManyToOne
    @JoinColumn(name = "tag_id")
    private Tag tag;

    public TagId() {
    }

    public TagId(Long postId, Tag tag) {
        this.postId = postId;
        this.tag = tag;
    }

    public Long getPostId() {
        return postId;
    }

    public void setPostId(Long postId) {
        this.postId = postId;
    }

    public Tag getTag() {
        return tag;
    }

    public void setTag(Tag tag) {
        this.tag = tag;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        TagId tagId = (TagId) o;
        return Objects.equals(postId, tagId.postId) && Objects.equals(tag, tagId.tag);
    }

    @Override
    public int hashCode() {
        return Objects.hash(postId, tag);
    }
}
