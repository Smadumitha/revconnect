package com.revconnect.postfeedservice.repository;

import com.revconnect.postfeedservice.entity.Post;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface PostRepository extends JpaRepository<Post, Long> {

    // Personalized Feed
    org.springframework.data.domain.Page<Post> findByUserIdIn(java.util.List<Long> userIds, org.springframework.data.domain.Pageable pageable);

    // Trending Posts
    org.springframework.data.domain.Page<Post> findByOrderByCreatedAtDesc(org.springframework.data.domain.Pageable pageable);

    // Promotional Posts
    org.springframework.data.domain.Page<Post> findByPromotionalTrue(org.springframework.data.domain.Pageable pageable);

    @Query("""
       SELECT p FROM Post p
       JOIN PostHashtag ph ON ph.postId = p.id
       JOIN Hashtag h ON h.id = ph.hashtagId
       WHERE h.tag = :tag
       """)
    org.springframework.data.domain.Page<Post> findPostsByHashtag(String tag, org.springframework.data.domain.Pageable pageable);
    org.springframework.data.domain.Page<Post> findByUserId(Long userId, org.springframework.data.domain.Pageable pageable);
}