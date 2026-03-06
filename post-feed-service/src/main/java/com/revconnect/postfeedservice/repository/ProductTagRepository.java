package com.revconnect.postfeedservice.repository;

import com.revconnect.postfeedservice.entity.ProductTag;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ProductTagRepository extends JpaRepository<ProductTag, Long> {

    List<ProductTag> findByPostId(Long postId);

}