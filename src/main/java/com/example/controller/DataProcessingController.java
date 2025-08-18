package com.example.controller;

import com.example.service.DataProcessingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/data-processing")
@CrossOrigin(origins = "*")
public class DataProcessingController {

    @Autowired
    private DataProcessingService dataProcessingService;

    @PostMapping("/excel-to-csv")
    public ResponseEntity<Map<String, Object>> convertExcelToCsv(@RequestParam("file") MultipartFile file) {
        try {
            // Validate file
            if (file.isEmpty()) {
                Map<String, Object> response = new HashMap<>();
                response.put("success", false);
                response.put("message", "Please select a file to upload");
                return ResponseEntity.badRequest().body(response);
            }

            // Validate file type
            String originalFilename = file.getOriginalFilename();
            if (originalFilename == null || (!originalFilename.endsWith(".xlsx") && !originalFilename.endsWith(".xls"))) {
                Map<String, Object> response = new HashMap<>();
                response.put("success", false);
                response.put("message", "Please upload an Excel file (.xlsx or .xls)");
                return ResponseEntity.badRequest().body(response);
            }

            String csvFilePath = dataProcessingService.convertExcelToCsv(file);
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "Excel file converted to CSV successfully");
            response.put("csvFilePath", csvFilePath);
            
            return ResponseEntity.ok(response);
        } catch (IOException e) {
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", "Error converting Excel to CSV: " + e.getMessage());
            
            return ResponseEntity.internalServerError().body(response);
        }
    }
}
