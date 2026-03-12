package com.revconnect.postfeedservice.service;

import com.revconnect.postfeedservice.client.ConnectionClient;
import com.revconnect.postfeedservice.dto.FollowerResponse;
import com.revconnect.postfeedservice.dto.PostResponse;
import com.revconnect.postfeedservice.entity.Post;
import com.revconnect.postfeedservice.repository.HashtagRepository;
import com.revconnect.postfeedservice.repository.PostRepository;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class FeedServiceTest {

    @Mock
    private PostRepository postRepository;

    @Mock
    private ConnectionClient connectionClient;

    @Mock
    private HashtagRepository hashtagRepository;

    @Mock
    private PostService postService;

    @InjectMocks
    private FeedService feedService;

    private Post mockPost;
    private PostResponse mockPostResponse;
    private FollowerResponse mockFollower;

    @Before
    public void setUp() {
        mockPost = Post.builder()
                .id(1L)
                .userId(2L)
                .content("Test content")
                .build();

        mockPostResponse = PostResponse.builder()
                .id(1L)
                .userId(2L)
                .content("Test content")
                .build();

        mockFollower = new FollowerResponse();
        mockFollower.setFollowerId(1L);
        mockFollower.setFollowingId(2L);
    }

    @Test
    public void testGetHomeFeed_Success() {
        Pageable pageable = PageRequest.of(0, 10);
        Page<Post> postPage = new PageImpl<>(List.of(mockPost));

        when(connectionClient.getFollowingUsers(1L)).thenReturn(List.of(mockFollower));
        when(postRepository.findByUserIdIn(anyList(), any(Pageable.class))).thenReturn(postPage);
        when(postService.toResponse(any(Post.class), anyLong())).thenReturn(mockPostResponse);

        Page<PostResponse> result = feedService.getHomeFeed(1L, pageable);

        assertNotNull(result);
        assertEquals(1, result.getContent().size());
        assertEquals("Test content", result.getContent().get(0).getContent());
    }

    @Test
    public void testGetTrendingPosts_Success() {
        Pageable pageable = PageRequest.of(0, 10);
        Page<Post> postPage = new PageImpl<>(List.of(mockPost));

        when(postRepository.findByOrderByCreatedAtDesc(pageable)).thenReturn(postPage);
        when(postService.toResponse(any(Post.class), anyLong())).thenReturn(mockPostResponse);

        Page<PostResponse> result = feedService.getTrendingPosts(1L, pageable);

        assertNotNull(result);
        assertEquals(1, result.getContent().size());
    }
}