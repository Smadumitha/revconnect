package com.revconnect.postfeedservice.repository;

import com.revconnect.postfeedservice.entity.PostHashtag;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface PostHashtagRepository extends JpaRepository<PostHashtag, Long> {
    List<PostHashtag> findByPostId(Long postId);
}