package com.example.controller;

import com.example.dto.ReportRequest;
import com.example.dto.StudentDto;
import com.example.service.ReportingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/reports")
@CrossOrigin(origins = "*")
public class ReportingController {

    @Autowired
    private ReportingService reportingService;

    @GetMapping("/students")
    public ResponseEntity<Map<String, Object>> getStudents(
            @RequestParam(defaultValue = "0") Integer page,
            @RequestParam(defaultValue = "20") Integer size,
            @RequestParam(required = false) Long studentId,
            @RequestParam(required = false) String className) {
        
        try {
            // Validate parameters
            if (page < 0) {
                Map<String, Object> response = new HashMap<>();
                response.put("success", false);
                response.put("message", "Page must be non-negative");
                return ResponseEntity.badRequest().body(response);
            }
            if (size <= 0) {
                Map<String, Object> response = new HashMap<>();
                response.put("success", false);
                response.put("message", "Size must be positive");
                return ResponseEntity.badRequest().body(response);
            }
            if (studentId != null && studentId <= 0) {
                Map<String, Object> response = new HashMap<>();
                response.put("success", false);
                response.put("message", "Student ID must be positive");
                return ResponseEntity.badRequest().body(response);
            }
            
            ReportRequest request = new ReportRequest(page, size, studentId, className);
            Page<StudentDto> studentsPage = reportingService.getStudents(request);
            long totalCount = reportingService.getTotalCount(request);
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("students", studentsPage.getContent());
            response.put("totalElements", studentsPage.getTotalElements());
            response.put("totalPages", studentsPage.getTotalPages());
            response.put("currentPage", studentsPage.getNumber());
            response.put("pageSize", studentsPage.getSize());
            response.put("filteredCount", totalCount);
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", "Error retrieving students: " + e.getMessage());
            
            return ResponseEntity.internalServerError().body(response);
        }
    }

    @PostMapping("/export/excel")
    public ResponseEntity<Map<String, Object>> exportToExcel(@RequestBody ReportRequest request) {
        try {
            // Validate request parameters
            if (!request.isValid()) {
                Map<String, Object> response = new HashMap<>();
                response.put("success", false);
                response.put("message", "Invalid request parameters: " + request.getValidationErrors());
                return ResponseEntity.badRequest().body(response);
            }
            
            String filePath = reportingService.exportToExcel(request);
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "Excel report generated successfully");
            response.put("filePath", filePath);
            
            return ResponseEntity.ok(response);
        } catch (IOException e) {
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", "Error generating Excel report: " + e.getMessage());
            
            return ResponseEntity.internalServerError().body(response);
        }
    }

    @PostMapping("/export/csv")
    public ResponseEntity<Map<String, Object>> exportToCsv(@RequestBody ReportRequest request) {
        try {
            // Validate request parameters
            if (!request.isValid()) {
                Map<String, Object> response = new HashMap<>();
                response.put("success", false);
                response.put("message", "Invalid request parameters: " + request.getValidationErrors());
                return ResponseEntity.badRequest().body(response);
            }
            
            String filePath = reportingService.exportToCsv(request);
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "CSV report generated successfully");
            response.put("filePath", filePath);
            
            return ResponseEntity.ok(response);
        } catch (IOException e) {
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", "Error generating CSV report: " + e.getMessage());
            
            return ResponseEntity.internalServerError().body(response);
        }
    }

    @PostMapping("/export/pdf")
    public ResponseEntity<Map<String, Object>> exportToPdf(@RequestBody ReportRequest request) {
        try {
            // Validate request parameters
            if (!request.isValid()) {
                Map<String, Object> response = new HashMap<>();
                response.put("success", false);
                response.put("message", "Invalid request parameters: " + request.getValidationErrors());
                return ResponseEntity.badRequest().body(response);
            }
            
            String filePath = reportingService.exportToPdf(request);
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "PDF report generated successfully");
            response.put("filePath", filePath);
            
            return ResponseEntity.ok(response);
        } catch (IOException e) {
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", "Error generating PDF report: " + e.getMessage());
            
            return ResponseEntity.internalServerError().body(response);
        }
    }

    @GetMapping("/download/{fileName}")
    public ResponseEntity<Resource> downloadFile(@PathVariable String fileName) {
        try {
            // Extract just the filename from the path
            String actualFileName = fileName;
            if (fileName.contains("/")) {
                actualFileName = fileName.substring(fileName.lastIndexOf("/") + 1);
            } else if (fileName.contains("\\")) {
                actualFileName = fileName.substring(fileName.lastIndexOf("\\") + 1);
            }
            
            // Resolve the file path
            Path filePath = Paths.get(System.getProperty("user.dir"), "reports", actualFileName);
            Resource resource = new UrlResource(filePath.toUri());
            
            if (resource.exists() && resource.isReadable()) {
                String contentType = "application/octet-stream";
                if (actualFileName.endsWith(".xlsx")) {
                    contentType = "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet";
                } else if (actualFileName.endsWith(".csv")) {
                    contentType = "text/csv";
                } else if (actualFileName.endsWith(".pdf")) {
                    contentType = "application/pdf";
                }
                
                return ResponseEntity.ok()
                    .contentType(MediaType.parseMediaType(contentType))
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + actualFileName + "\"")
                    .body(resource);
            } else {
                return ResponseEntity.notFound().build();
            }
        } catch (MalformedURLException e) {
            return ResponseEntity.badRequest().build();
        }
    }
}
