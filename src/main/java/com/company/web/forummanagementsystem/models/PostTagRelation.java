package com.company.web.forummanagementsystem.models;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;

import java.util.Objects;
@Entity
@Table(name = "posts_tags")
public class PostTagRelation {

    @EmbeddedId
    private TagId postTagId;
    @JsonIgnore
    @ManyToOne
    @MapsId("id")
    private Post post;

    public PostTagRelation() {
    }

    public PostTagRelation(TagId postTagId) {
        this.postTagId = postTagId;
    }

    public TagId getPostTagId() {
        return postTagId;
    }

    public void setPostTagId(TagId postTagId) {
        this.postTagId = postTagId;
    }

    public Post getPost() {
        return post;
    }

    public void setPost(Post post) {
        this.post = post;
    }
}
