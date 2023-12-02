package com.charbz.service;

import com.charbz.model.User;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
@RequiredArgsConstructor
public class UserService {

    private static final String BASE_URL = "http://localhost:8080";
    private final RestTemplate restTemplate;

    public ResponseEntity<Boolean> login(String username, String password) {
        return restTemplate.getForEntity(BASE_URL + "/login/{username}/{password}", Boolean.class, username, password);
    }

    public ResponseEntity<Boolean> signup(String username, String password) {
        return restTemplate.getForEntity(BASE_URL + "/signup/{username}/{password}", Boolean.class, username, password);
    }

    public ResponseEntity<User> getUser(String username) {
        return restTemplate.getForEntity(BASE_URL + "/user/{username}", User.class, username);
    }

    public void updateUserInfo(User user) {
        HttpEntity<User> requestEntity = new HttpEntity<>(user);
        restTemplate.put(BASE_URL + "/user", requestEntity, User.class);
    }
}
