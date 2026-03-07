package com.revconnect.postfeedservice.entity;


import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(name="hashtags")
@Data
public class Hashtag {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    public Hashtag(Long id, String tag) {
        this.id = id;
        this.tag = tag;
    }
    public Hashtag(){

    }

    private String tag;

    public String getTag() {
        return tag;
    }

    public void setTag(String tag) {
        this.tag = tag;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }



}