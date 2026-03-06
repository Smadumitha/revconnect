package com.revconnect.postfeedservice.service;

import com.revconnect.postfeedservice.dto.PostRequest;
import com.revconnect.postfeedservice.entity.Post;
import com.revconnect.postfeedservice.entity.Hashtag;
import com.revconnect.postfeedservice.repository.PostRepository;
import com.revconnect.postfeedservice.repository.HashtagRepository;
import com.revconnect.postfeedservice.repository.PostHashtagRepository;

import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Optional;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

class PostServiceTest {

    @Mock
    private PostRepository postRepository;

    @Mock
    private HashtagRepository hashtagRepository;

    @Mock
    private PostHashtagRepository postHashtagRepository;

    @InjectMocks
    private PostService postService;

    public PostServiceTest() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testCreatePost() {

        PostRequest request = new PostRequest();
        request.setUserId(1L);
        request.setContent("Test post #java");

        Post post = Post.builder()
                .id(1L)
                .content("Test post #java")
                .userId(1L)
                .build();

        when(postRepository.save(any(Post.class))).thenReturn(post);
        when(hashtagRepository.findByTag("java")).thenReturn(Optional.of(new Hashtag()));

        var response = postService.createPost(request);

        assertNotNull(response);
    }
}