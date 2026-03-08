package com.revconnect.connectionservice.controller;

import com.revconnect.connectionservice.dto.ConnectionRequestDTO;
import com.revconnect.connectionservice.entity.ConnectionRequest;
import com.revconnect.connectionservice.entity.Follower;
import com.revconnect.connectionservice.service.ConnectionService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/connections")
public class ConnectionController {

    private final ConnectionService connectionService;

    public ConnectionController(ConnectionService connectionService) {
        this.connectionService = connectionService;
    }

    @GetMapping("/test")
    public String test() {
        return "Connection Service Working";
    }
    @GetMapping
    public List<Long> getConnections(@RequestParam Long userId){
        return connectionService.getConnections(userId);
    }
    @GetMapping("/pending/received")
    public List<ConnectionRequest> getPendingReceived(@RequestParam Long userId){
        return connectionService.getPendingReceived(userId);
    }

    @GetMapping("/pending/sent")
    public List<ConnectionRequest> getPendingSent(@RequestParam Long userId){
        return connectionService.getPendingSent(userId);
    }
    @DeleteMapping("/{id}")
    public String removeConnection(@PathVariable Long id){
        connectionService.removeConnection(id);
        return "Connection removed successfully";
    }
    @PostMapping("/request")
    public ConnectionRequestDTO sendRequest(
            @RequestBody ConnectionRequestDTO request){

        return connectionService.sendRequest(
                request.getSenderId(),
                request.getReceiverId());
    }

    @PutMapping("/accept/{requestId}")
    public ConnectionRequestDTO acceptRequest(@PathVariable Long requestId) {
        return connectionService.acceptRequest(requestId);
    }

    @PutMapping("/reject/{requestId}")
    public ConnectionRequestDTO rejectRequest(@PathVariable Long requestId) {
        return connectionService.rejectRequest(requestId);
    }

    @GetMapping("/followers/{userId}")
    public List<Follower> getFollowers(@PathVariable Long userId) {
        return connectionService.getFollowers(userId);
    }

    @GetMapping("/following/{userId}")
    public List<Follower> getFollowing(@PathVariable Long userId) {
        return connectionService.getFollowing(userId);
    }

    @DeleteMapping("/unfollow")
    public void unfollow(
            @RequestParam Long followerId,
            @RequestParam Long followingId) {

        connectionService.unfollow(followerId, followingId);
    }

    @GetMapping("/mutual")
    public List<Follower> mutual(
            @RequestParam Long user1,
            @RequestParam Long user2){

        return connectionService.getMutualConnections(user1,user2);
    }
}