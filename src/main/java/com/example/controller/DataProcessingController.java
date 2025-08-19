package com.example.controller;

import com.example.service.DataProcessingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.Path;
import java.nio.file.Paths;
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
        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", "Error processing Excel file: " + e.getMessage());
            
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
            Path filePath = Paths.get(System.getProperty("user.dir"), "csv", actualFileName);
            Resource resource = new UrlResource(filePath.toUri());
            
            if (resource.exists() && resource.isReadable()) {
                return ResponseEntity.ok()
                    .contentType(MediaType.parseMediaType("text/csv"))
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
