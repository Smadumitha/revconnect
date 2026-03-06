package com.revconnect.postfeedservice.repository;

import com.revconnect.postfeedservice.entity.PostHashtag;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PostHashtagRepository extends JpaRepository<PostHashtag, Long> {
}