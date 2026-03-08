package com.revconnect.postfeedservice.repository;

import com.revconnect.postfeedservice.entity.Post;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface PostRepository extends JpaRepository<Post, Long> {

    // Personalized Feed
    List<Post> findByUserIdIn(List<Long> userIds);

    // Trending Posts
    List<Post> findTop10ByOrderByCreatedAtDesc();

    // Promotional Posts
    List<Post> findByPromotionalTrue();

    @Query("""
       SELECT p FROM Post p
       JOIN PostHashtag ph ON ph.postId = p.id
       JOIN Hashtag h ON h.id = ph.hashtagId
       WHERE h.tag = :tag
       """)
    List<Post> findPostsByHashtag(String tag);
    List<Post> findByUserId(Long userId);
}