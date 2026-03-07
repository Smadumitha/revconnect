package com.revconnect.connectionservice.service;

import com.revconnect.connectionservice.dto.ConnectionRequestDTO;
import com.revconnect.connectionservice.entity.ConnectionRequest;
import com.revconnect.connectionservice.entity.Follower;
import com.revconnect.connectionservice.repository.ConnectionRequestRepository;
import com.revconnect.connectionservice.repository.FollowerRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class ConnectionServiceTest {

    @Mock
    private ConnectionRequestRepository requestRepo;

    @Mock
    private FollowerRepository followerRepo;

    @InjectMocks
    private ConnectionService connectionService;

    @BeforeEach
    void setup() {
        MockitoAnnotations.openMocks(this);
    }


    @Test
    void testSendRequestSuccess() {

        ConnectionRequest request = new ConnectionRequest();
        request.setId(1L);
        request.setSenderId(1L);
        request.setReceiverId(2L);
        request.setStatus("PENDING");
        request.setCreatedAt(LocalDateTime.now());

        when(requestRepo.existsBySenderIdAndReceiverId(1L,2L)).thenReturn(false);
        when(requestRepo.existsBySenderIdAndReceiverId(2L,1L)).thenReturn(false);
        when(followerRepo.findByFollowerId(1L)).thenReturn(List.of());
        when(requestRepo.save(any())).thenReturn(request);

        ConnectionRequestDTO result = connectionService.sendRequest(1L,2L);

        assertEquals("PENDING", result.getStatus());
    }


    @Test
    void testSelfRequestNotAllowed(){

        assertThrows(ResponseStatusException.class, () ->
                connectionService.sendRequest(1L,1L));
    }


    @Test
    void testDuplicateRequest(){

        when(requestRepo.existsBySenderIdAndReceiverId(1L,2L)).thenReturn(true);

        assertThrows(ResponseStatusException.class, () ->
                connectionService.sendRequest(1L,2L));
    }


    @Test
    void testAcceptRequest(){

        ConnectionRequest request = new ConnectionRequest();
        request.setId(5L);
        request.setSenderId(1L);
        request.setReceiverId(2L);
        request.setStatus("PENDING");

        when(requestRepo.findById(5L)).thenReturn(Optional.of(request));

        ConnectionRequestDTO result = connectionService.acceptRequest(5L);

        assertEquals("ACCEPTED", result.getStatus());
        verify(followerRepo, times(1)).save(any(Follower.class));
    }


    @Test
    void testRejectRequest() {

        ConnectionRequest request = new ConnectionRequest();
        request.setId(6L);
        request.setSenderId(2L);
        request.setReceiverId(3L);
        request.setStatus("PENDING");

        ConnectionRequest savedRequest = new ConnectionRequest();
        savedRequest.setId(6L);
        savedRequest.setSenderId(2L);
        savedRequest.setReceiverId(3L);
        savedRequest.setStatus("REJECTED");

        when(requestRepo.findById(6L)).thenReturn(Optional.of(request));
        when(requestRepo.save(any(ConnectionRequest.class))).thenReturn(savedRequest);

        ConnectionRequestDTO result = connectionService.rejectRequest(6L);

        assertEquals("REJECTED", result.getStatus());
    }


    @Test
    void testGetFollowers(){

        Follower follower = new Follower();
        follower.setFollowerId(1L);
        follower.setFollowingId(2L);

        when(followerRepo.findByFollowingId(2L)).thenReturn(List.of(follower));

        List<Follower> result = connectionService.getFollowers(2L);

        assertEquals(1,result.size());
    }


    @Test
    void testGetFollowing(){

        Follower follower = new Follower();
        follower.setFollowerId(1L);
        follower.setFollowingId(2L);

        when(followerRepo.findByFollowerId(1L)).thenReturn(List.of(follower));

        List<Follower> result = connectionService.getFollowing(1L);

        assertEquals(1,result.size());
    }


    @Test
    void testMutualConnections(){

        Follower f1 = new Follower();
        f1.setFollowerId(1L);
        f1.setFollowingId(5L);

        Follower f2 = new Follower();
        f2.setFollowerId(3L);
        f2.setFollowingId(5L);

        when(followerRepo.findByFollowerId(1L)).thenReturn(List.of(f1));
        when(followerRepo.findByFollowerId(3L)).thenReturn(List.of(f2));

        List<Follower> result = connectionService.getMutualConnections(1L,3L);

        assertEquals(1,result.size());
    }
}