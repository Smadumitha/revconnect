package com.revconnect.postfeedservice.controller;

import com.revconnect.postfeedservice.dto.PostResponse;
import com.revconnect.postfeedservice.service.PostService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(PostController.class)
class PostControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private PostService postService;

    @Test
    void testGetPost() throws Exception {

        PostResponse response = PostResponse.builder()
                .id(1L)
                .content("Test Post")
                .build();

        when(postService.getPostById(org.mockito.ArgumentMatchers.eq(1L), org.mockito.ArgumentMatchers.isNull())).thenReturn(response);

        mockMvc.perform(get("/posts/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").value("Test Post"));
    }
}