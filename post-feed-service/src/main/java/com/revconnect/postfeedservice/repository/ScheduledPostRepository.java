package com.revconnect.postfeedservice.repository;

import com.revconnect.postfeedservice.entity.ScheduledPost;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;

public interface ScheduledPostRepository extends JpaRepository<ScheduledPost, Long> {

    List<ScheduledPost> findByScheduledTimeBeforeAndPublishedFalse(LocalDateTime time);

}