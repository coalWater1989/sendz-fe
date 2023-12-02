package com.charbz.service;

import com.charbz.model.Send;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.List;

@Service
@RequiredArgsConstructor
public class SendzService {

    private static final String BASE_URL = "http://localhost:8080";
    private final RestTemplate restTemplate;

    public ResponseEntity<List> getColors() {
        return restTemplate.getForEntity(BASE_URL + "/colors", List.class);
    }

    public ResponseEntity<List> getBurns() {
        return restTemplate.getForEntity(BASE_URL + "/burns", List.class);
    }

    public ResponseEntity<List> getBoulderTypes() {
        return restTemplate.getForEntity(BASE_URL + "/boulderTypes", List.class);
    }

    public ResponseEntity<List> getGrades() {
        return restTemplate.getForEntity(BASE_URL + "/grades", List.class);
    }

    public ResponseEntity<List> getGyms() {
        return restTemplate.getForEntity(BASE_URL + "/gyms", List.class);
    }

    public ResponseEntity<List> getHoldTypes() {
        return restTemplate.getForEntity(BASE_URL + "/holdTypes", List.class);
    }

    public ResponseEntity<List> getWallTypes() {
        return restTemplate.getForEntity(BASE_URL + "/wallTypes", List.class);
    }

    public ResponseEntity<List> getSends(String username) {
        return restTemplate.getForEntity(BASE_URL + "/sends/{username}", List.class, username);
    }

    public void logSend(Send send) {
        HttpEntity<Send> requestEntity = new HttpEntity<>(send);
        restTemplate.postForEntity(BASE_URL + "/send", requestEntity, Send.class);
    }
}
