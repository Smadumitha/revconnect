package com.revconnect.connectionservice.service;

import com.revconnect.connectionservice.client.UserClient;
import com.revconnect.connectionservice.dto.ConnectionRequestDTO;
import com.revconnect.connectionservice.dto.ConnectionStatusDTO;
import com.revconnect.connectionservice.dto.UserProfileResponse;
import com.revconnect.connectionservice.entity.ConnectionRequest;
import com.revconnect.connectionservice.entity.Follower;
import com.revconnect.connectionservice.exception.ResourceNotFoundException;
import com.revconnect.connectionservice.repository.ConnectionRequestRepository;
import com.revconnect.connectionservice.repository.FollowerRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class ConnectionService {
    private final ConnectionRequestRepository requestRepo;
    private final FollowerRepository followerRepo;
    @Autowired
    private UserClient userClient;

    public ConnectionService(ConnectionRequestRepository requestRepo,
                             FollowerRepository followerRepo) {
        this.requestRepo = requestRepo;
        this.followerRepo = followerRepo;
    }

    public ConnectionRequestDTO sendRequest(Long senderId, Long receiverId) {
        UserProfileResponse sender;
        UserProfileResponse receiver;
        try {
            sender = userClient.getUserProfile(senderId);
            receiver = userClient.getUserProfile(receiverId);
        } catch (Exception e) {
            throw new ResourceNotFoundException("User not found");
        }
        if(sender == null || receiver == null) throw new ResourceNotFoundException("User not found");
        if(senderId.equals(receiverId)) throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Self connection not allowed");

        if(followerRepo.existsByFollowerIdAndFollowingId(senderId, receiverId)){
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Already following");
        }

        Optional<ConnectionRequest> existing = requestRepo.findBySenderIdAndReceiverId(senderId, receiverId);
        if(existing.isPresent()){
            if("PENDING".equals(existing.get().getStatus())) throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Request already pending");
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Request already exists with status: " + existing.get().getStatus());
        }

        ConnectionRequest request = new ConnectionRequest();
        request.setSenderId(senderId);
        request.setReceiverId(receiverId);
        request.setStatus("PENDING");
        request.setCreatedAt(LocalDateTime.now());
        return mapToDTO(requestRepo.save(request));
    }

    public ConnectionRequestDTO acceptRequest(Long requestId) {
        ConnectionRequest request = requestRepo.findById(requestId)
                .orElseThrow(() -> new ResourceNotFoundException("Request not found"));
        request.setStatus("ACCEPTED");
        requestRepo.save(request);

        Follower follower = new Follower();
        follower.setFollowerId(request.getSenderId());
        follower.setFollowingId(request.getReceiverId());
        follower.setCreatedAt(LocalDateTime.now());
        followerRepo.save(follower);

        return mapToDTO(request);
    }

    public ConnectionRequestDTO rejectRequest(Long requestId) {
        ConnectionRequest request = requestRepo.findById(requestId)
                .orElseThrow(() -> new ResourceNotFoundException("Request not found"));
        request.setStatus("REJECTED");
        return mapToDTO(requestRepo.save(request));
    }

    public List<Follower> getFollowers(Long userId) { return followerRepo.findByFollowingId(userId); }
    public List<Follower> getFollowing(Long userId) { return followerRepo.findByFollowerId(userId); }

    public void unfollow(Long followerId, Long followingId) {
        followerRepo.findByFollowerId(followerId).stream()
                .filter(f -> f.getFollowingId().equals(followingId))
                .forEach(followerRepo::delete);
        
        requestRepo.findBySenderIdAndReceiverId(followerId, followingId)
            .ifPresent(r -> { r.setStatus("REMOVED"); requestRepo.save(r); });
    }

    public List<Follower> getMutualConnections(Long user1, Long user2) {
        List<Follower> u1f = followerRepo.findByFollowerId(user1);
        List<Follower> u2f = followerRepo.findByFollowerId(user2);
        return u1f.stream().filter(f1 -> u2f.stream().anyMatch(f2 -> f2.getFollowingId().equals(f1.getFollowingId()))).toList();
    }

    public List<Long> getConnections(Long userId){
        List<Long> connections = new java.util.ArrayList<>();
        followerRepo.findByFollowingId(userId).forEach(f -> connections.add(f.getFollowerId()));
        followerRepo.findByFollowerId(userId).forEach(f -> connections.add(f.getFollowingId()));
        return connections;
    }

    public ConnectionStatusDTO getConnectionStatus(Long userId, Long targetId) {
        boolean isFollowing = followerRepo.existsByFollowerIdAndFollowingId(userId, targetId);
        boolean isConnected = isConnected(userId, targetId);
        boolean isPendingSent = requestRepo.findBySenderIdAndReceiverIdAndStatus(userId, targetId, "PENDING").isPresent();
        boolean isPendingReceived = requestRepo.findBySenderIdAndReceiverIdAndStatus(targetId, userId, "PENDING").isPresent();

        return ConnectionStatusDTO.builder()
                .isFollowing(isFollowing)
                .isConnected(isConnected)
                .isPendingSent(isPendingSent)
                .isPendingReceived(isPendingReceived)
                .build();
    }

    public boolean isConnected(Long userId, Long targetId) {
        return requestRepo.findBySenderIdAndReceiverIdAndStatus(userId, targetId, "ACCEPTED").isPresent() ||
               requestRepo.findBySenderIdAndReceiverIdAndStatus(targetId, userId, "ACCEPTED").isPresent();
    }

    public List<ConnectionRequestDTO> getPendingReceived(Long userId){ 
        return requestRepo.findByReceiverIdAndStatus(userId,"PENDING").stream()
                .map(this::mapToDTO)
                .toList();
    }
    
    public List<ConnectionRequestDTO> getPendingSent(Long userId){ 
        return requestRepo.findBySenderIdAndStatus(userId,"PENDING").stream()
                .map(this::mapToDTO)
                .toList();
    }
    
    public void removeConnection(Long id){ followerRepo.delete(followerRepo.findById(id).orElseThrow(() -> new ResourceNotFoundException("Not found"))); }

    private ConnectionRequestDTO mapToDTO(ConnectionRequest request){
        ConnectionRequestDTO dto = new ConnectionRequestDTO();
        dto.setId(request.getId());
        dto.setSenderId(request.getSenderId());
        dto.setReceiverId(request.getReceiverId());
        dto.setStatus(request.getStatus());
        dto.setCreatedAt(request.getCreatedAt());
        
        try {
            dto.setRequester(userClient.getUserProfile(request.getSenderId()));
            dto.setRecipient(userClient.getUserProfile(request.getReceiverId()));
        } catch (Exception e) {
            // Log or ignore
        }
        
        return dto;
    }
}