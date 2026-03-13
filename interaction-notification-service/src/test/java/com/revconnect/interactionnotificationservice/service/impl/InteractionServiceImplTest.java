package com.revconnect.interactionnotificationservice.service.impl;

import com.revconnect.interactionnotificationservice.client.PostServiceClient;
import com.revconnect.interactionnotificationservice.client.UserServiceClient;
import com.revconnect.interactionnotificationservice.dto.InteractionEvent;
import com.revconnect.interactionnotificationservice.dto.UserDTO;
import com.revconnect.interactionnotificationservice.entity.Interaction;
import com.revconnect.interactionnotificationservice.event.InteractionEventProducer;
import com.revconnect.interactionnotificationservice.repository.InteractionRepository;
import com.revconnect.interactionnotificationservice.service.NotificationService;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class InteractionServiceImplTest {

    @Mock
    private InteractionRepository interactionRepository;

    @Mock
    private NotificationService notificationService;

    @Mock
    private PostServiceClient postServiceClient;

    @Mock
    private UserServiceClient userServiceClient;

    @Mock
    private InteractionEventProducer interactionEventProducer;

    @InjectMocks
    private InteractionServiceImpl interactionService;

    private Interaction mockInteraction;

    @Before
    public void setUp() {
        mockInteraction = Interaction.builder()
                .id(1L)
                .userId(1L)
                .postId(1L)
                .type("LIKE")
                .createdAt(LocalDateTime.now())
                .build();
    }

    @Test
    public void testLikePost_Success() {
        when(postServiceClient.getPostOwnerId(1L)).thenReturn(2L);
        when(interactionRepository.findByUserIdAndPostIdAndType(1L, 1L, "LIKE")).thenReturn(Optional.empty());
        when(interactionRepository.save(any(Interaction.class))).thenReturn(mockInteraction);

        String result = interactionService.likePost(1L, 1L);

        assertEquals("Post liked successfully", result);
        verify(interactionEventProducer, times(1)).sendInteractionEvent(any(InteractionEvent.class));
        verify(notificationService, times(1)).createNotification(anyLong(), anyLong(), anyString(), anyString());
    }

    @Test
    public void testUnlikePost_Success() {
        when(interactionRepository.findByUserIdAndPostIdAndType(1L, 1L, "LIKE")).thenReturn(Optional.of(mockInteraction));

        String result = interactionService.unlikePost(1L, 1L);

        assertEquals("Post unliked successfully", result);
        verify(interactionRepository, times(1)).delete(mockInteraction);
        verify(interactionEventProducer, times(1)).sendInteractionEvent(any(InteractionEvent.class));
    }

    @Test
    public void testGetLikeCount_Success() {
        when(interactionRepository.countByPostIdAndType(1L, "LIKE")).thenReturn(5L);

        long count = interactionService.getLikeCount(1L);

        assertEquals(5L, count);
    }

    @Test
    public void testHasLiked_Success() {
        when(interactionRepository.findByUserIdAndPostIdAndType(1L, 1L, "LIKE")).thenReturn(Optional.of(mockInteraction));

        boolean hasLiked = interactionService.hasLiked(1L, 1L);

        assertTrue(hasLiked);
    }

    @Test
    public void testGetLikerNames_Success() {
        when(interactionRepository.findByPostIdAndType(1L, "LIKE")).thenReturn(List.of(mockInteraction));
        
        UserDTO userDTO = new UserDTO();
        userDTO.setId(1L);
        userDTO.setUsername("testuser");
        
        when(userServiceClient.getUserById(1L)).thenReturn(userDTO);

        List<String> likers = interactionService.getLikerNames(1L);

        assertEquals(1, likers.size());
        assertEquals("testuser", likers.get(0));
    }
}
