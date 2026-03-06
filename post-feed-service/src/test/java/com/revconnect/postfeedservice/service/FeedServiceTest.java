package com.revconnect.postfeedservice.service;

import com.revconnect.postfeedservice.entity.Post;
import com.revconnect.postfeedservice.repository.PostRepository;
import com.revconnect.postfeedservice.dto.PostResponse;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.List;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

class FeedServiceTest {

    @Mock
    private PostRepository postRepository;

    @InjectMocks
    private FeedService feedService;

    @BeforeEach
    void setup(){
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testHomeFeed(){

        Post post = Post.builder()
                .id(1L)
                .userId(1L)
                .content("Test feed post")
                .build();

        List<Long> following = List.of(1L,2L,3L);

        when(postRepository.findByUserIdIn(following))
                .thenReturn(List.of(post));

        List<PostResponse> result = feedService.getHomeFeed(1L);

        assertEquals(1, result.size());
    }
}