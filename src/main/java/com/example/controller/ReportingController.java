package com.example.controller;

import com.example.dto.ReportRequest;
import com.example.dto.StudentDto;
import com.example.service.ReportingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
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
}
