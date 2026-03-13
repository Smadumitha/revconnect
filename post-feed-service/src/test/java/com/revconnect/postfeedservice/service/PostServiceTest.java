package com.revconnect.postfeedservice.service;

import com.revconnect.postfeedservice.client.InteractionClient;
import com.revconnect.postfeedservice.client.UserClient;
import com.revconnect.postfeedservice.dto.AuthorDTO;
import com.revconnect.postfeedservice.dto.PostRequest;
import com.revconnect.postfeedservice.dto.PostResponse;
import com.revconnect.postfeedservice.entity.Hashtag;
import com.revconnect.postfeedservice.entity.Post;
import com.revconnect.postfeedservice.entity.PostHashtag;
import com.revconnect.postfeedservice.repository.*;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class PostServiceTest {

    @Mock
    private PostHashtagRepository postHashtagRepository;
    @Mock
    private InteractionClient interactionClient;
    @Mock
    private UserClient userClient;
    @Mock
    private PostRepository postRepository;
    @Mock
    private HashtagRepository hashtagRepository;
    @Mock
    private ProductTagRepository productTagRepository;
    @Mock
    private ScheduledPostRepository scheduledPostRepository;

    @InjectMocks
    private PostService postService;

    private Post mockPost;
    private PostRequest mockRequest;
    private AuthorDTO mockAuthor;

    @Before
    public void setUp() {
        mockPost = Post.builder()
                .id(1L)
                .userId(1L)
                .content("Hello #world")
                .mediaUrl("")
                .promotional(false)
                .pinned(false)
                .createdAt(LocalDateTime.now())
                .build();

        mockRequest = new PostRequest();
        mockRequest.setUserId(1L);
        mockRequest.setContent("Hello #world");
        mockRequest.setPromotional(false);
        mockRequest.setPinned(false);

        mockAuthor = new AuthorDTO();
        mockAuthor.setId(1L);
        mockAuthor.setDisplayName("Test User");
    }

    @Test
    public void testCreatePost_Success() {
        Hashtag hashtag = new Hashtag(1L, "world");
        
        when(postRepository.save(any(Post.class))).thenReturn(mockPost);
        when(hashtagRepository.findByTag("world")).thenReturn(Optional.of(hashtag));
        when(postHashtagRepository.save(any(PostHashtag.class))).thenReturn(new PostHashtag());

        PostResponse response = postService.createPost(mockRequest);

        assertNotNull(response);
        assertEquals(Long.valueOf(1L), response.getId());
        verify(postRepository).save(any(Post.class));
        verify(postHashtagRepository, atLeastOnce()).save(any(PostHashtag.class));
    }

    @Test
    public void testGetPostById_Success() {
        when(postRepository.findById(1L)).thenReturn(Optional.of(mockPost));
        when(interactionClient.getLikeCount(1L)).thenReturn(5L);
        when(interactionClient.getCommentCount(1L)).thenReturn(2L);
        when(interactionClient.getShareCount(1L)).thenReturn(1L);
        when(userClient.getUserById(1L)).thenReturn(mockAuthor);
        when(postHashtagRepository.findByPostId(1L)).thenReturn(Collections.emptyList());
        when(productTagRepository.findByPostId(1L)).thenReturn(Collections.emptyList());

        PostResponse response = postService.getPostById(1L, 2L);

        assertNotNull(response);
        assertEquals(Long.valueOf(1L), response.getId());
        assertEquals("Hello #world", response.getContent());
        assertEquals(5, response.getLikesCount());
        assertNotNull(response.getAuthor());
        assertEquals("Test User", response.getAuthor().getDisplayName());
    }

    @Test
    public void testDeletePost_Success() {
        when(postRepository.findById(1L)).thenReturn(Optional.of(mockPost));

        postService.deletePost(1L);

        verify(postRepository).delete(mockPost);
    }
}