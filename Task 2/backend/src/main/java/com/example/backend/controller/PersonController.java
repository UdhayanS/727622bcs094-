package com.example.backend.controller;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import java.util.*;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api")
@CrossOrigin(origins = "http://localhost:3000")
public class PersonController {

    @Autowired
    private RestTemplate restTemplate;

    private final String TOKEN = "6319e44e8458cdcf787fe9dc2102d896fa2423a70bdc1a3dd731f0f778a22d4b";
    private final String USER_API = "https://gorest.co.in/public/v2/users";
    private final String POST_API = "https://gorest.co.in/public/v2/posts";
    private final String COMMENT_API = "https://gorest.co.in/public/v2/comments";

    private final ObjectMapper objectMapper = new ObjectMapper();

    private <T> List<T> fetchList(String url, TypeReference<List<T>> typeRef) throws Exception {
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + TOKEN);
        HttpEntity<String> entity = new HttpEntity<>(headers);

        ResponseEntity<String> response = restTemplate.exchange(
                url, HttpMethod.GET, entity, String.class);

        return objectMapper.readValue(response.getBody(), typeRef);
    }

    @GetMapping("/users")
    public ResponseEntity<?> getTopUsersByPostCount() {
        try {
            List<Map<String, Object>> users = fetchList(USER_API, new TypeReference<>() {});
            List<Map<String, Object>> posts = fetchList(POST_API, new TypeReference<>() {});

            Map<Integer, Long> userPostCounts = posts.stream()
                    .map(post -> (Integer) post.get("user_id"))
                    .filter(Objects::nonNull)
                    .collect(Collectors.groupingBy(userId -> userId, Collectors.counting()));

            List<Map<String, Object>> topUsers = users.stream()
                    .peek(user -> user.put("postCount", userPostCounts.getOrDefault(user.get("id"), 0L)))
                    .sorted((u1, u2) -> Long.compare(
                            (Long) u2.get("postCount"),
                            (Long) u1.get("postCount")))
                    .limit(5)
                    .collect(Collectors.toList());

            return ResponseEntity.ok(topUsers);

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error: " + e.getMessage());
        }
    }

    @GetMapping("/posts")
    public ResponseEntity<?> getTrendingPostsByCommentCount() {
        try {
            List<Map<String, Object>> posts = fetchList(POST_API, new TypeReference<>() {});
            List<Map<String, Object>> comments = fetchList(COMMENT_API, new TypeReference<>() {});

            // Count comments per post
            Map<Integer, Long> postCommentCounts = comments.stream()
                    .map(comment -> (Integer) comment.get("post_id"))
                    .filter(Objects::nonNull)
                    .collect(Collectors.groupingBy(postId -> postId, Collectors.counting()));

            // Add commentCount to each post
            List<Map<String, Object>> trendingPosts = posts.stream()
                    .peek(post -> post.put("commentCount", postCommentCounts.getOrDefault(post.get("id"), 0L)))
                    .sorted((p1, p2) -> Long.compare(
                            (Long) p2.get("commentCount"),
                            (Long) p1.get("commentCount")))
                    .limit(6)
                    .collect(Collectors.toList());

            return ResponseEntity.ok(trendingPosts);

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error: " + e.getMessage());
        }
    }
}
