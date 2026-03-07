package com.revconnect.connectionservice.repository;

import com.revconnect.connectionservice.entity.Follower;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface FollowerRepository
        extends JpaRepository<Follower, Long> {

    List<Follower> findByFollowerId(Long followerId);

    List<Follower> findByFollowingId(Long followingId);
    boolean existsByFollowerIdAndFollowingId(Long followerId, Long followingId);
}