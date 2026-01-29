package org.example.coursework1.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

@Service
public class IlpService {
    private final RestTemplate restTemplate = new RestTemplate();

    public List<Map<String, Object>> fetchDrones(String urlPath) {
        if (urlPath == null || !urlPath.startsWith("http")) {
            throw new IllegalArgumentException("Invalid URL path provided");
        }

        ResponseEntity<Map[]> response = restTemplate.getForEntity(urlPath, Map[].class);
        Map<String, Object>[] body = response.getBody();
        return body == null ? List.of() : Arrays.asList(body);
    }
}
