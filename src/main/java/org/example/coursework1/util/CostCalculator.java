// src/main/java/org/example/coursework1/util/CostCalculator.java
package org.example.coursework1.util;

import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class CostCalculator {

    public List<Map<String, Object>> costPer100Moves(List<Map<String, Object>> drones) {
        List<Map<String, Object>> processedDrones = new ArrayList<>();

        for (Map<String, Object> drone : drones) {
            Map<String, Object> processedDrone = new HashMap<>(drone);

            // 提取 capability 数据
            Object capabilityObj = drone.get("capability");
            if (capabilityObj instanceof Map) {
                Map<String, Object> capability = (Map<String, Object>) capabilityObj;

                // 获取成本相关数值，处理 NaN 情况
                double costInitial = getDoubleValue(capability, "costInitial", 0.0);
                double costFinal = getDoubleValue(capability, "costFinal", 0.0);
                double costPerMove = getDoubleValue(capability, "costPerMove", 0.0);

                // 计算 costPer100Moves
                double costPer100Moves = costInitial + costFinal + (costPerMove * 100);

                // 将计算结果添加到无人机数据中
                processedDrone.put("costPer100Moves", costPer100Moves);
            } else {
                // 如果没有 capability 或格式不正确，默认 costPer100Moves 为 0
                processedDrone.put("costPer100Moves", 0.0);
            }

            processedDrones.add(processedDrone);
        }

        return processedDrones;
    }

    private double getDoubleValue(Map<String, Object> map, String key, double defaultValue) {
        Object value = map.get(key);
        if (value == null) {
            return defaultValue;
        }

        if (value instanceof Number) {
            return ((Number) value).doubleValue();
        } else if (value instanceof String) {
            try {
                return Double.parseDouble((String) value);
            } catch (NumberFormatException e) {
                return defaultValue;
            }
        }
        return defaultValue;
    }
}
