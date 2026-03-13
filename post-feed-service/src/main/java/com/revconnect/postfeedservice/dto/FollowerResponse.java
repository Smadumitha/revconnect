package com.revconnect.postfeedservice.dto;

import lombok.Data;

@Data
public class FollowerResponse {

    private Long id;
    private Long followerId;
    private Long followingId;

}