package com.example.controller;

import com.example.service.PerformanceMonitoringService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/performance")
public class PerformanceController {

    @Autowired
    private PerformanceMonitoringService performanceMonitoringService;

    @GetMapping("/metrics")
    public ResponseEntity<Map<String, Object>> getPerformanceMetrics() {
        Map<String, Object> metrics = performanceMonitoringService.getPerformanceMetrics();
        return ResponseEntity.ok(metrics);
    }

    @GetMapping("/recommendations")
    public ResponseEntity<Map<String, Object>> getOptimizationRecommendations() {
        Map<String, Object> recommendations = performanceMonitoringService.getOptimizationRecommendations();
        return ResponseEntity.ok(recommendations);
    }
}
