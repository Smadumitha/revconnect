package com.revconnect.postfeedservice.repository;

import com.revconnect.postfeedservice.entity.Hashtag;
import com.revconnect.postfeedservice.entity.Post;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface HashtagRepository extends JpaRepository<Hashtag, Long> {

    Optional<Hashtag> findByTag(String tag);
    @Query("""
SELECT p FROM Post p
JOIN PostHashtag ph ON ph.postId = p.id
JOIN Hashtag h ON h.id = ph.hashtagId
WHERE h.tag = :tag
""")
    List<Post> findPostsByHashtag(String tag);

    @Query("""
SELECT h.tag
FROM Hashtag h
JOIN PostHashtag ph ON ph.hashtagId = h.id
GROUP BY h.tag
ORDER BY COUNT(ph.id) DESC
""")
    List<String> findTrendingTags();
}