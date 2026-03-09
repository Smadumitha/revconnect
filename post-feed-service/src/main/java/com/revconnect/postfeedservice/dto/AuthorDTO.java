package com.revconnect.postfeedservice.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AuthorDTO {
    private Long id;
    private String username;
    private String displayName;
    private String profilePicture;
    private String role;
}
