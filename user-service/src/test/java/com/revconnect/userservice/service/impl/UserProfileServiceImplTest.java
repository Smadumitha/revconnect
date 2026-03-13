package com.revconnect.userservice.service.impl;

import com.revconnect.userservice.client.ConnectionClient;
import com.revconnect.userservice.dto.UserProfileResponse;
import com.revconnect.userservice.entity.UserProfile;
import com.revconnect.userservice.repository.PrivacySettingsRepository;
import com.revconnect.userservice.repository.UserProfileRepository;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class UserProfileServiceImplTest {

    @Mock
    private UserProfileRepository userProfileRepository;

    @Mock
    private PrivacySettingsRepository privacySettingsRepository;

    @Mock
    private ConnectionClient connectionClient;

    @InjectMocks
    private UserProfileServiceImpl userProfileService;

    private UserProfile mockUser;

    @Before
    public void setUp() {
        mockUser = new UserProfile();
        mockUser.setUserId(1L);
        mockUser.setUsername("testuser");
        mockUser.setDisplayName("Test User");
        mockUser.setRole("PERSONAL");
    }

    @Test
    public void testGetUserByUsername_Success() {
        when(userProfileRepository.findByUsername("testuser")).thenReturn(Optional.of(mockUser));
        when(connectionClient.getFollowers(anyLong())).thenReturn(Collections.emptyList());
        when(connectionClient.getFollowing(anyLong())).thenReturn(Collections.emptyList());

        UserProfileResponse response = userProfileService.getUserByUsername("testuser");

        assertNotNull(response);
        assertEquals("testuser", response.getUsername());
        assertEquals("Test User", response.getDisplayName());
    }

    @Test
    public void testGetSuggestedUsers_Success() {
        when(userProfileRepository.findAll()).thenReturn(List.of(mockUser));
        when(connectionClient.getFollowers(anyLong())).thenReturn(Collections.emptyList());
        when(connectionClient.getFollowing(anyLong())).thenReturn(Collections.emptyList());

        List<UserProfileResponse> responses = userProfileService.getSuggestedUsers(2L);

        assertNotNull(responses);
        assertEquals(1, responses.size());
        assertEquals("testuser", responses.get(0).getUsername());
    }

    @Test
    public void testGetUserProfile_Success() {
        when(userProfileRepository.findByUserId(1L)).thenReturn(Optional.of(mockUser));
        when(connectionClient.getFollowers(anyLong())).thenReturn(Collections.emptyList());
        when(connectionClient.getFollowing(anyLong())).thenReturn(Collections.emptyList());

        UserProfileResponse response = userProfileService.getUserProfile(1L);

        assertNotNull(response);
        assertEquals(Long.valueOf(1L), response.getUserId());
        assertEquals("testuser", response.getUsername());
    }
}
