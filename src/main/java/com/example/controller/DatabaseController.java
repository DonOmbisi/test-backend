package com.example.controller;

import com.example.entity.Student;
import com.example.service.DatabaseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/database")
@CrossOrigin(origins = "*")
public class DatabaseController {

    @Autowired
    private DatabaseService databaseService;

    @PostMapping("/upload-csv")
    public ResponseEntity<Map<String, Object>> uploadCsvToDatabase(@RequestParam("file") MultipartFile file) {
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
            if (originalFilename == null || !originalFilename.endsWith(".csv")) {
                Map<String, Object> response = new HashMap<>();
                response.put("success", false);
                response.put("message", "Please upload a CSV file (.csv)");
                return ResponseEntity.badRequest().body(response);
            }

            int recordsUploaded = databaseService.uploadCsvToDatabase(file);
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "CSV file uploaded to database successfully");
            response.put("recordsUploaded", recordsUploaded);
            
            return ResponseEntity.ok(response);
        } catch (IOException e) {
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", "Error uploading CSV to database: " + e.getMessage());
            
            return ResponseEntity.internalServerError().body(response);
        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", "Error processing CSV file: " + e.getMessage());
            
            return ResponseEntity.internalServerError().body(response);
        }
    }

    @GetMapping("/total-count")
    public ResponseEntity<Map<String, Object>> getTotalStudentCount() {
        try {
            long totalCount = databaseService.getTotalStudentCount();
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("totalCount", totalCount);
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", "Error getting total count: " + e.getMessage());
            
            return ResponseEntity.internalServerError().body(response);
        }
    }

    @GetMapping("/students")
    public ResponseEntity<Map<String, Object>> getAllStudents() {
        try {
            List<Student> students = databaseService.getAllStudents();
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("students", students);
            response.put("totalCount", students.size());
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", "Error getting students: " + e.getMessage());
            
            return ResponseEntity.internalServerError().body(response);
        }
    }
}
