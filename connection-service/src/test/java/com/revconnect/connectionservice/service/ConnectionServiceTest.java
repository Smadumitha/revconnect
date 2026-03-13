package com.revconnect.connectionservice.service;

import com.revconnect.connectionservice.client.UserClient;
import com.revconnect.connectionservice.dto.ConnectionRequestDTO;
import com.revconnect.connectionservice.dto.ConnectionStatusDTO;
import com.revconnect.connectionservice.dto.UserProfileResponse;
import com.revconnect.connectionservice.entity.ConnectionRequest;
import com.revconnect.connectionservice.entity.Follower;
import com.revconnect.connectionservice.repository.ConnectionRequestRepository;
import com.revconnect.connectionservice.repository.FollowerRepository;
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
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class ConnectionServiceTest {

    @Mock
    private ConnectionRequestRepository requestRepo;

    @Mock
    private FollowerRepository followerRepo;

    @Mock
    private UserClient userClient;

    @InjectMocks
    private ConnectionService connectionService;

    private UserProfileResponse mockSender;
    private UserProfileResponse mockReceiver;
    private ConnectionRequest mockRequest;
    private Follower mockFollower;

    @Before
    public void setUp() {
        mockSender = new UserProfileResponse();
        mockSender.setUserId(1L);
        mockSender.setUsername("sender");

        mockReceiver = new UserProfileResponse();
        mockReceiver.setUserId(2L);
        mockReceiver.setUsername("receiver");

        mockRequest = new ConnectionRequest();
        mockRequest.setId(10L);
        mockRequest.setSenderId(1L);
        mockRequest.setReceiverId(2L);
        mockRequest.setStatus("PENDING");
        mockRequest.setCreatedAt(LocalDateTime.now());

        mockFollower = new Follower();
        mockFollower.setId(20L);
        mockFollower.setFollowerId(1L);
        mockFollower.setFollowingId(2L);
    }

    @Test
    public void testSendRequest_Success() {
        when(userClient.getUserProfile(1L)).thenReturn(mockSender);
        when(userClient.getUserProfile(2L)).thenReturn(mockReceiver);
        when(followerRepo.existsByFollowerIdAndFollowingId(1L, 2L)).thenReturn(false);
        when(requestRepo.findBySenderIdAndReceiverId(1L, 2L)).thenReturn(Optional.empty());
        when(requestRepo.save(any(ConnectionRequest.class))).thenReturn(mockRequest);

        ConnectionRequestDTO dto = connectionService.sendRequest(1L, 2L);

        assertNotNull(dto);
        assertEquals(Long.valueOf(10L), dto.getId());
        assertEquals("PENDING", dto.getStatus());
    }

    @Test
    public void testAcceptRequest_Success() {
        when(requestRepo.findById(10L)).thenReturn(Optional.of(mockRequest));
        when(requestRepo.save(any(ConnectionRequest.class))).thenReturn(mockRequest);
        when(followerRepo.save(any(Follower.class))).thenReturn(mockFollower);
        when(userClient.getUserProfile(1L)).thenReturn(mockSender);
        when(userClient.getUserProfile(2L)).thenReturn(mockReceiver);

        ConnectionRequestDTO dto = connectionService.acceptRequest(10L);

        assertEquals("ACCEPTED", dto.getStatus());
        verify(followerRepo).save(any(Follower.class));
    }

    @Test
    public void testRejectRequest_Success() {
        when(requestRepo.findById(10L)).thenReturn(Optional.of(mockRequest));
        when(requestRepo.save(any(ConnectionRequest.class))).thenReturn(mockRequest);
        when(userClient.getUserProfile(1L)).thenReturn(mockSender);
        when(userClient.getUserProfile(2L)).thenReturn(mockReceiver);

        ConnectionRequestDTO dto = connectionService.rejectRequest(10L);

        assertEquals("REJECTED", dto.getStatus());
    }

    @Test
    public void testGetFollowers_Success() {
        when(followerRepo.findByFollowingId(2L)).thenReturn(List.of(mockFollower));
        List<Follower> followers = connectionService.getFollowers(2L);
        assertEquals(1, followers.size());
    }

    @Test
    public void testGetFollowing_Success() {
        when(followerRepo.findByFollowerId(1L)).thenReturn(List.of(mockFollower));
        List<Follower> following = connectionService.getFollowing(1L);
        assertEquals(1, following.size());
    }

    @Test
    public void testGetConnectionStatus_Success() {
        when(followerRepo.existsByFollowerIdAndFollowingId(1L, 2L)).thenReturn(true);
        when(requestRepo.findBySenderIdAndReceiverIdAndStatus(1L, 2L, "ACCEPTED")).thenReturn(Optional.of(mockRequest));

        ConnectionStatusDTO status = connectionService.getConnectionStatus(1L, 2L);

        assertTrue(status.isFollowing());
        assertTrue(status.isConnected());
    }

    @Test
    public void testUnfollow_Success() {
        when(followerRepo.findByFollowerId(1L)).thenReturn(List.of(mockFollower));
        when(requestRepo.findBySenderIdAndReceiverId(1L, 2L)).thenReturn(Optional.of(mockRequest));

        connectionService.unfollow(1L, 2L);

        verify(followerRepo).delete(mockFollower);
        verify(requestRepo).save(mockRequest);
        assertEquals("REMOVED", mockRequest.getStatus());
    }
}