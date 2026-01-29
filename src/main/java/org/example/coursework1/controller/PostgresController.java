package org.example.coursework1.controller;

import org.example.coursework1.service.PostgresService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/acp")
public class PostgresController {

    @Autowired
    private PostgresService postgresService;

    @GetMapping("/all/postgres/{table}")
    public ResponseEntity<List<Map<String, Object>>> getAllPostgresData(@PathVariable String table) {
        return postgresService.getAllDataFromTableResponse(table);
    }
    @PostMapping("/process/postgres/{table}")
    public ResponseEntity<List<Map<String, Object>>> InsertPostgresData(@PathVariable String table,  @RequestBody Map<String, Object> body) {
        String url = body.get("urlPath").toString();
        return postgresService.insertDataFromILPToPostgres(table,url);
    }
}
