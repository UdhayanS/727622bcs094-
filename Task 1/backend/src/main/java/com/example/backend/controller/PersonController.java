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

    private final String TOKEN = "f2b6f1cd2d4ff46de48bad2cd4fc7f353d167a8a5d8f1c6bc108c58c65a08dc1";
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
    public ResponseEntity<?> getTopUsersByCommentsOnPosts() {
        try {
            List<Map<String, Object>> users = fetchList(USER_API, new TypeReference<>() {
            });
            List<Map<String, Object>> posts = fetchList(POST_API, new TypeReference<>() {
            });
            List<Map<String, Object>> comments = fetchList(COMMENT_API, new TypeReference<>() {
            });

            // Map post ID to user ID
            Map<Integer, Integer> postToUser = posts.stream()
                    .collect(Collectors.toMap(
                            post -> (Integer) post.get("id"),
                            post -> (Integer) post.get("user_id")));

            // Count comments per user (based on post ownership)
            Map<Integer, Long> userCommentCounts = comments.stream()
                    .map(comment -> postToUser.get((Integer) comment.get("post_id")))
                    .filter(Objects::nonNull)
                    .collect(Collectors.groupingBy(userId -> userId, Collectors.counting()));

            // Sort and map top users
            List<Map<String, Object>> topUsers = users.stream()
                    .peek(user -> user.put("commentCount", userCommentCounts.getOrDefault(user.get("id"), 0L)))
                    .sorted((u1, u2) -> Long.compare(
                            (Long) u2.get("commentCount"),
                            (Long) u1.get("commentCount")))
                    .limit(5)
                    .collect(Collectors.toList());

            return ResponseEntity.ok(topUsers);

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error: " + e.getMessage());
        }
    }

    @GetMapping("/posts")
    public ResponseEntity<?> getPostsByType(@RequestParam("type") String type) {
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.set("Authorization", "Bearer " + TOKEN);
            HttpEntity<String> entity = new HttpEntity<>(headers);

            // Fetch all posts
            ResponseEntity<String> postsResponse = restTemplate.exchange(
                    POST_API, HttpMethod.GET, entity, String.class);
            List<Map<String, Object>> posts = objectMapper.readValue(
                    postsResponse.getBody(), new TypeReference<>() {
                    });

            if ("latest".equalsIgnoreCase(type)) {
                // Sort by created_at (if available), else by id descending
                List<Map<String, Object>> latestPosts = posts.stream()
                        .sorted((p1, p2) -> {
                            String c1 = (String) p1.getOrDefault("created_at", "");
                            String c2 = (String) p2.getOrDefault("created_at", "");
                            return c2.compareTo(c1); // Newest first
                        })
                        .limit(5)
                        .collect(Collectors.toList());
                return ResponseEntity.ok(latestPosts);
            }

            if ("popular".equalsIgnoreCase(type)) {
                // Fetch all comments
                ResponseEntity<String> commentsResponse = restTemplate.exchange(
                        COMMENT_API, HttpMethod.GET, entity, String.class);
                List<Map<String, Object>> comments = objectMapper.readValue(
                        commentsResponse.getBody(), new TypeReference<>() {
                        });

                // Count comments per post
                Map<Integer, Long> commentCountMap = comments.stream()
                        .collect(Collectors.groupingBy(
                                c -> (Integer) c.get("post_id"),
                                Collectors.counting()));

                // Find max count
                long maxComments = commentCountMap.values().stream()
                        .max(Long::compareTo)
                        .orElse(0L);

                // Filter posts with max comment count
                List<Map<String, Object>> mostCommentedPosts = posts.stream()
                        .filter(post -> commentCountMap.getOrDefault((Integer) post.get("id"), 0L) == maxComments)
                        .peek(post -> post.put("commentCount", maxComments))
                        .collect(Collectors.toList());

                return ResponseEntity.ok(mostCommentedPosts);
            }

            return ResponseEntity.badRequest().body("Invalid type: " + type);

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error: " + e.getMessage());
        }
    }

}
