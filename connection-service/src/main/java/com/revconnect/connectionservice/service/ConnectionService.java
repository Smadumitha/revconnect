package com.revconnect.connectionservice.service;

import com.revconnect.connectionservice.dto.ConnectionRequestDTO;
import com.revconnect.connectionservice.entity.ConnectionRequest;
import com.revconnect.connectionservice.entity.Follower;
import com.revconnect.connectionservice.exception.ResourceNotFoundException;
import com.revconnect.connectionservice.repository.ConnectionRequestRepository;
import com.revconnect.connectionservice.repository.FollowerRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class ConnectionService {

    private final ConnectionRequestRepository requestRepo;
    private final FollowerRepository followerRepo;

    public ConnectionService(ConnectionRequestRepository requestRepo,
                             FollowerRepository followerRepo) {
        this.requestRepo = requestRepo;
        this.followerRepo = followerRepo;
    }

    public ConnectionRequestDTO sendRequest(Long senderId, Long receiverId) {

        if(senderId.equals(receiverId)){
            throw new RuntimeException("You cannot send request to yourself");
        }

        if(requestRepo.existsBySenderIdAndReceiverId(senderId, receiverId)){
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Connection request already sent"
            );
        }

        if(requestRepo.existsBySenderIdAndReceiverIdOrSenderIdAndReceiverId(
                senderId, receiverId,
                receiverId, senderId)) {

            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Connection request already exists or pending"
            );
        }

        ConnectionRequest request = new ConnectionRequest();
        request.setSenderId(senderId);
        request.setReceiverId(receiverId);
        request.setStatus("PENDING");
        request.setCreatedAt(LocalDateTime.now());

        ConnectionRequest saved = requestRepo.save(request);

        return mapToDTO(saved);
    }

    public ConnectionRequestDTO acceptRequest(Long requestId) {

        ConnectionRequest request = requestRepo.findById(requestId)
                .orElseThrow(() -> new ResourceNotFoundException("Connection request not found"));

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
                .orElseThrow(() -> new ResourceNotFoundException("Connection request not found"));

        request.setStatus("REJECTED");

        ConnectionRequest saved = requestRepo.save(request);

        return mapToDTO(saved);
    }

    public List<Follower> getFollowers(Long userId) {
        return followerRepo.findByFollowingId(userId);
    }

    public List<Follower> getFollowing(Long userId) {
        return followerRepo.findByFollowerId(userId);
    }

    public void unfollow(Long followerId, Long followingId) {

        List<Follower> relations = followerRepo.findByFollowerId(followerId);

        relations.stream()
                .filter(f -> f.getFollowingId().equals(followingId))
                .forEach(followerRepo::delete);
    }

    public List<Follower> getMutualConnections(Long user1, Long user2) {
        List<Follower> user1Following = followerRepo.findByFollowerId(user1);
        List<Follower> user2Following = followerRepo.findByFollowerId(user2);

        return user1Following.stream()
                .filter(f1 -> user2Following.stream()
                        .anyMatch(f2 -> f2.getFollowingId().equals(f1.getFollowingId())))
                .toList();
    }

    private ConnectionRequestDTO mapToDTO(ConnectionRequest request){

        ConnectionRequestDTO dto = new ConnectionRequestDTO();

        dto.setId(request.getId());
        dto.setSenderId(request.getSenderId());
        dto.setReceiverId(request.getReceiverId());
        dto.setStatus(request.getStatus());
        dto.setCreatedAt(request.getCreatedAt());

        return dto;
    }
}