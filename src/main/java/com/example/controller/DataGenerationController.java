package com.example.controller;

import com.example.dto.DataGenerationRequest;
import com.example.service.DataGenerationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/data-generation")
@CrossOrigin(origins = "*")
public class DataGenerationController {

    @Autowired
    private DataGenerationService dataGenerationService;

    @PostMapping("/generate-excel")
    public ResponseEntity<Map<String, Object>> generateExcelFile(@RequestBody DataGenerationRequest request) {
        try {
            long startTime = System.currentTimeMillis();
            
            String filePath = dataGenerationService.generateExcelFile(request.getNumberOfRecords());
            
            long endTime = System.currentTimeMillis();
            long duration = endTime - startTime;
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "Excel file generated successfully");
            response.put("filePath", filePath);
            response.put("recordsGenerated", request.getNumberOfRecords());
            response.put("generationTimeMs", duration);
            response.put("generationTimeSeconds", duration / 1000.0);
            response.put("recordsPerSecond", Math.round((double) request.getNumberOfRecords() / (duration / 1000.0)));
            
            return ResponseEntity.ok(response);
        } catch (IOException e) {
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", "Error generating Excel file: " + e.getMessage());
            
            return ResponseEntity.internalServerError().body(response);
        }
    }

    @PostMapping("/performance-test")
    public ResponseEntity<Map<String, Object>> performanceTest() {
        try {
            Map<String, Object> results = new HashMap<>();
            results.put("success", true);
            results.put("message", "Performance test completed");
            
            // Test with different record sizes
            int[] testSizes = {1000, 10000, 100000, 1000000};
            
            for (int size : testSizes) {
                try {
                    long startTime = System.currentTimeMillis();
                    String filePath = dataGenerationService.generateExcelFile(size);
                    long endTime = System.currentTimeMillis();
                    long duration = endTime - startTime;
                    
                    Map<String, Object> testResult = new HashMap<>();
                    testResult.put("recordsGenerated", size);
                    testResult.put("generationTimeMs", duration);
                    testResult.put("generationTimeSeconds", duration / 1000.0);
                    testResult.put("recordsPerSecond", Math.round((double) size / (duration / 1000.0)));
                    testResult.put("filePath", filePath);
                    
                    results.put("test_" + size, testResult);
                } catch (Exception e) {
                    Map<String, Object> testResult = new HashMap<>();
                    testResult.put("error", e.getMessage());
                    results.put("test_" + size, testResult);
                }
            }
            
            return ResponseEntity.ok(results);
        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", "Error during performance test: " + e.getMessage());
            
            return ResponseEntity.internalServerError().body(response);
        }
    }
}
