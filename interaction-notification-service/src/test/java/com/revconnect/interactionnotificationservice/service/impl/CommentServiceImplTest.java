package com.revconnect.interactionnotificationservice.service.impl;

import com.revconnect.interactionnotificationservice.client.PostServiceClient;
import com.revconnect.interactionnotificationservice.client.UserServiceClient;
import com.revconnect.interactionnotificationservice.dto.CommentRequestDTO;
import com.revconnect.interactionnotificationservice.dto.CommentResponseDTO;
import com.revconnect.interactionnotificationservice.dto.InteractionEvent;
import com.revconnect.interactionnotificationservice.dto.UserDTO;
import com.revconnect.interactionnotificationservice.entity.Comment;
import com.revconnect.interactionnotificationservice.event.InteractionEventProducer;
import com.revconnect.interactionnotificationservice.repository.CommentRepository;
import com.revconnect.interactionnotificationservice.service.NotificationService;
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

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class CommentServiceImplTest {

    @Mock
    private CommentRepository commentRepository;

    @Mock
    private NotificationService notificationService;

    @Mock
    private PostServiceClient postServiceClient;

    @Mock
    private UserServiceClient userServiceClient;

    @Mock
    private InteractionEventProducer interactionEventProducer;

    @InjectMocks
    private CommentServiceImpl commentService;

    private Comment mockComment;
    private CommentRequestDTO mockRequest;

    @Before
    public void setUp() {
        mockComment = Comment.builder()
                .id(1L)
                .userId(1L)
                .postId(1L)
                .content("Test Comment")
                .createdAt(LocalDateTime.now())
                .build();

        mockRequest = new CommentRequestDTO();
        mockRequest.setUserId(1L);
        mockRequest.setPostId(1L);
        mockRequest.setContent("Test Comment");
    }

    @Test
    public void testAddComment_Success() {
        when(commentRepository.save(any(Comment.class))).thenReturn(mockComment);
        when(postServiceClient.getPostOwnerId(1L)).thenReturn(2L);
        
        UserDTO author = new UserDTO();
        author.setId(1L);
        author.setDisplayName("Test User");
        when(userServiceClient.getUserById(1L)).thenReturn(author);

        CommentResponseDTO response = commentService.addComment(mockRequest);

        assertNotNull(response);
        assertEquals("Test Comment", response.getContent());
        assertNotNull(response.getAuthor());
        assertEquals("Test User", response.getAuthor().getDisplayName());

        verify(interactionEventProducer, times(1)).sendInteractionEvent(any(InteractionEvent.class));
        verify(notificationService, times(1)).createNotification(anyLong(), anyLong(), anyString(), anyString());
    }

    @Test
    public void testDeleteComment_Success() {
        when(commentRepository.existsById(1L)).thenReturn(true);

        String result = commentService.deleteComment(1L);

        assertEquals("Comment deleted successfully", result);
        verify(commentRepository, times(1)).deleteById(1L);
    }

    @Test
    public void testGetPostComments_Success() {
        Pageable pageable = PageRequest.of(0, 10);
        Page<Comment> commentPage = new PageImpl<>(List.of(mockComment));

        when(commentRepository.findByPostIdAndParentCommentIdIsNull(1L, pageable)).thenReturn(commentPage);

        Page<CommentResponseDTO> result = commentService.getPostComments(1L, pageable);

        assertNotNull(result);
        assertEquals(1, result.getContent().size());
        assertEquals("Test Comment", result.getContent().get(0).getContent());
    }

    @Test
    public void testGetCommentCount_Success() {
        when(commentRepository.countByPostId(1L)).thenReturn(3L);

        long count = commentService.getCommentCount(1L);

        assertEquals(3L, count);
    }

    @Test
    public void testGetPostCommentsList_Success() {
        when(commentRepository.findByPostIdOrderByCreatedAtAsc(1L)).thenReturn(List.of(mockComment));

        List<CommentResponseDTO> list = commentService.getPostCommentsList(1L);

        assertNotNull(list);
        assertEquals(1, list.size());
        assertEquals("Test Comment", list.get(0).getContent());
    }
}
