package org.example.coursework1.service;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.Arrays;
import java.util.List;

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

    public List<Map<String, Object>> fetchDronesPlain(String urlPath) {
        if (urlPath == null || !urlPath.startsWith("http")) {
            throw new IllegalArgumentException("Invalid URL path provided");
        }

        ResponseEntity<Map[]> response = restTemplate.getForEntity(urlPath, Map[].class);
        Map<String, Object>[] body = response.getBody();

        if (body == null) {
            return List.of();
        }

        // 处理每个 drone 对象，将 capability 属性扁平化
        return Arrays.stream(body)
                .map(this::flattenCapability)
                .collect(Collectors.toList());
    }

    /**
     * 将 capability 对象的属性合并到顶层对象
     */
    private Map<String, Object> flattenCapability(Map<String, Object> drone) {
        Map<String, Object> flattened = new HashMap<>(drone);

        // 获取 capability 对象
        Object capabilityObj = flattened.remove("capability");
        if (capabilityObj instanceof Map) {
            Map<String, Object> capability = (Map<String, Object>) capabilityObj;
            // 将 capability 中的键值对合并到顶层对象
            flattened.putAll(capability);
        }

        return flattened;
    }
}
