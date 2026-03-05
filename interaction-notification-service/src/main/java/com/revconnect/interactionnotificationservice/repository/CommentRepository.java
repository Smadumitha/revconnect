package com.revconnect.interactionnotificationservice.repository;

import com.revconnect.interactionnotificationservice.entity.Comment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;


public interface CommentRepository extends JpaRepository<Comment, Long> {
    Page<Comment> findByPostIdAndParentCommentIdIsNull(Long postId, Pageable pageable);
}