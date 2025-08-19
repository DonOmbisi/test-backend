package com.example.controller;

import com.example.dto.DataGenerationRequest;
import com.example.service.DataGenerationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;

import org.springframework.core.io.Resource;

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
            
            // Test with smaller, more manageable sizes for faster testing
            int[] testSizes = {100, 500, 1000, 5000};
            
            for (int size : testSizes) {
                try {
                    long startTime = System.currentTimeMillis();
                    // Use the optimized method for performance testing
                    String filePath = dataGenerationService.generateExcelFileFast(size);
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

    @PostMapping("/quick-performance-test")
    public ResponseEntity<Map<String, Object>> quickPerformanceTest() {
        try {
            Map<String, Object> results = new HashMap<>();
            results.put("success", true);
            results.put("message", "Quick performance test completed");
            
            // Test with very small sizes for instant results
            int[] testSizes = {10, 50, 100, 200};
            String lastFilePath = null;
            
            for (int size : testSizes) {
                try {
                    long startTime = System.currentTimeMillis();
                    String filePath = dataGenerationService.generateExcelFileFast(size);
                    long endTime = System.currentTimeMillis();
                    long duration = endTime - startTime;
                    
                    // Store the last file path for download
                    lastFilePath = filePath;
                    
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
            
            // Add the last file path to the main results for download functionality
            if (lastFilePath != null) {
                results.put("filePath", lastFilePath);
                results.put("recordsGenerated", testSizes[testSizes.length - 1]);
            }
            
            return ResponseEntity.ok(results);
        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", "Error during quick performance test: " + e.getMessage());
            
            return ResponseEntity.internalServerError().body(response);
        }
    }

    @GetMapping("/download/{fileName}")
    public ResponseEntity<Resource> downloadFile(@PathVariable String fileName) {
        try {
            // Extract just the filename if a full path was passed
            String actualFileName = fileName;
            if (fileName.contains("/") || fileName.contains("\\")) {
                // Extract filename from path
                if (fileName.contains("/")) {
                    actualFileName = fileName.substring(fileName.lastIndexOf("/") + 1);
                } else if (fileName.contains("\\")) {
                    actualFileName = fileName.substring(fileName.lastIndexOf("\\") + 1);
                }
            }
            
            String storagePath = dataGenerationService.getStoragePath();
            Path filePath = Paths.get(storagePath, "excel", actualFileName);
            
            if (!Files.exists(filePath)) {
                return ResponseEntity.notFound().build();
            }
            
            Resource resource = new org.springframework.core.io.FileSystemResource(filePath.toFile());
            
            return ResponseEntity.ok()
                .header("Content-Disposition", "attachment; filename=\"" + actualFileName + "\"")
                .header("Content-Type", "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet")
                .body(resource);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }
}
