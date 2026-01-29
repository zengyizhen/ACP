package org.example.coursework1.controller;

import org.example.coursework1.service.ProcessService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/acp")
public class ProcessController {
    @Autowired
    ProcessService processService;

    @PostMapping("/process/dump")
    ResponseEntity<List<Map<String, Object>>> dumpProcessData(@RequestBody(required = false) Map<String, Object> body){
        String urlPath = body.get("urlPath").toString();
        return processService.dumpProcessData(urlPath);
    }
}
