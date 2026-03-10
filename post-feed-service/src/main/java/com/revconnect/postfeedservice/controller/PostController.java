package com.revconnect.postfeedservice.controller;

import com.revconnect.postfeedservice.dto.PostRequest;
import com.revconnect.postfeedservice.dto.PostResponse;
import com.revconnect.postfeedservice.service.PostService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

@RestController
@RequestMapping("/posts")
@RequiredArgsConstructor
public class PostController {

    private final PostService postService;
    @GetMapping("/{postId}/owner")
    public Long getPostOwner(@PathVariable Long postId){
        return postService.getPostOwnerId(postId);
    }

    @GetMapping("/user/{userId}")
    public List<PostResponse> getPostsByUser(@PathVariable Long userId, @RequestParam(required = false) Long currentUserId){
        return postService.getPostsByUser(userId, currentUserId);
    }
    @GetMapping("/test")
    public String test(){
        return "Post service working";
    }

    @PostMapping
    public ResponseEntity<PostResponse> createPost(@RequestBody PostRequest request){
        return ResponseEntity.ok(postService.createPost(request));
    }

    @PutMapping("/{id}")
    public ResponseEntity<PostResponse> updatePost(@PathVariable Long id,
                                                   @RequestBody PostRequest request){
        return ResponseEntity.ok(postService.updatePost(id, request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> deletePost(@PathVariable Long id){
        postService.deletePost(id);
        return ResponseEntity.ok("Post deleted successfully");
    }

    @GetMapping("/{id}")
    public ResponseEntity<PostResponse> getPost(@PathVariable Long id, @RequestParam(required = false) Long userId){
        return ResponseEntity.ok(postService.getPostById(id, userId));
    }

    @PostMapping("/{postId}/media")
    public ResponseEntity<String> uploadMedia(@PathVariable Long postId,
                                              @RequestParam("file") MultipartFile file) throws IOException {
        String filename = "post_" + postId + "_" + file.getOriginalFilename();
        Path path = Paths.get("uploads/" + filename);
        Files.createDirectories(path.getParent());
        Files.write(path, file.getBytes());
        String url = "/uploads/" + filename;
        postService.updateMediaUrl(postId, url);
        return ResponseEntity.ok(url);
    }
}