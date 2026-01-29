package org.example.coursework1.service;

import org.example.coursework1.util.CostCalculator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
@Service
public class ProcessService {
    @Autowired
    private IlpService ilpService;

    @Autowired
    private CostCalculator costCalculator;
    public ResponseEntity<List<Map<String, Object>>> dumpProcessData(String urlPath) {
        try {

            System.out.println("Received urlPath: " + urlPath);
            if (urlPath == null) {
                System.out.println("ERROR: urlPath is null");
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();//改成400
            }

            // 获取数据 from url（格式未知）--》object（json表格内容)
            List<Map<String, Object>> drones = ilpService.fetchDrones(urlPath);

            // 计算 costPer100Moves
            List<Map<String, Object>> processedDrones = costCalculator.costPer100Moves(drones);

            return ResponseEntity.ok(processedDrones);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }
}
