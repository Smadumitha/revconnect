package com.revconnect.postfeedservice.entity;


import jakarta.persistence.*;
import lombok.Data;



@Entity
@Table(name="product_tags")
@Data
public class ProductTag {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    public ProductTag(Long id, Long postId, String productName, String productUrl) {
        this.id = id;
        this.postId = postId;
        this.productName = productName;
        this.productUrl = productUrl;
    }
    public ProductTag(){}

    private Long postId;

    private String productName;
    private String productUrl;
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

    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public String getProductUrl() {
        return productUrl;
    }

    public void setProductUrl(String productUrl) {
        this.productUrl = productUrl;
    }


}