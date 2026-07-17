package com.koky.web.web;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/status")
public class StatusController {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @GetMapping("/health")
    public Map<String, Object> healthCheck() {

        Map<String, Object> response = new HashMap<>();
        response.put("status", "UP");
        response.put("time", LocalDateTime.now().toString());

        try {
            jdbcTemplate.execute("SELECT 1");
            response.put("database", "CONNECTED");
        } catch (Exception e) {
            response.put("database", "ERROR");
        }

        return response;
    }
}
