package com.revconnect.postfeedservice.entity;


import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(name="post_hashtags")
@Data
public class PostHashtag {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    public PostHashtag(Long id, Long postId, Long hashtagId) {
        this.id = id;
        this.postId = postId;
        this.hashtagId = hashtagId;
    }
    public PostHashtag(){

    }

    private Long postId;
    private Long hashtagId;


    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getPostId() {
        return postId;
    }

    public void setPostId(Long postId) {
        this.postId = postId;
    }

    public Long getHashtagId() {
        return hashtagId;
    }

    public void setHashtagId(Long hashtagId) {
        this.hashtagId = hashtagId;
    }


}
