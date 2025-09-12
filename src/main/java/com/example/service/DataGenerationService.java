package com.example.service;

import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.BufferedOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.concurrent.ThreadLocalRandom;

@Service
public class DataGenerationService {

    @Value("${app.file.storage.windows}")
    private String windowsStoragePath;

    @Value("${app.file.storage.linux}")
    private String linuxStoragePath;

    @Value("${app.file.storage.excel}")
    private String excelFolder;

    private static final String[] CLASSES = {"Class1", "Class2", "Class3", "Class4", "Class5"};
    
    // Pre-generated random data for better performance
    private static final String[] FIRST_NAMES = {"John", "Jane", "Mike", "Sarah", "David", "Lisa", "Tom", "Emma", "Alex", "Anna"};
    private static final String[] LAST_NAMES = {"Smith", "Johnson", "Williams", "Brown", "Jones", "Garcia", "Miller", "Davis", "Rodriguez", "Martinez"};
    private static final String[] DATE_PATTERNS = {"2000-01-15", "2001-03-22", "2002-07-08", "2003-11-30", "2004-05-12", 
                                                   "2005-09-18", "2006-12-03", "2007-04-25", "2008-08-14", "2009-10-07"};

    public String generateExcelFile(int numberOfRecords) throws IOException {
        // Create storage directory
        String storagePath = getStoragePath();
        Path excelPath = Paths.get(storagePath, excelFolder);
        Files.createDirectories(excelPath);

        // Generate filename
        String fileName = "students_" + System.currentTimeMillis() + ".xlsx";
        Path filePath = excelPath.resolve(fileName);

        // Optimized for 1M+ records: minimal memory footprint
        try (SXSSFWorkbook workbook = new SXSSFWorkbook(10)) { // Keep only 10 rows in memory for maximum performance
            workbook.setCompressTempFiles(true); // Compress temp files to save disk space
            Sheet sheet = workbook.createSheet("Students");

            // Create header row
            Row headerRow = sheet.createRow(0);
            String[] headers = {"studentId", "firstName", "lastName", "DOB", "class", "score"};
            for (int i = 0; i < headers.length; i++) {
                Cell cell = headerRow.createCell(i);
                cell.setCellValue(headers[i]);
            }

            // Use ThreadLocalRandom for better performance than Random
            ThreadLocalRandom random = ThreadLocalRandom.current();
            
            // Generate data rows directly without pre-allocation for memory efficiency
            for (int i = 1; i <= numberOfRecords; i++) {
                Row row = sheet.createRow(i);
                
                // studentId
                row.createCell(0).setCellValue(i);
                
                // firstName (direct array access)
                row.createCell(1).setCellValue(FIRST_NAMES[random.nextInt(FIRST_NAMES.length)]);
                
                // lastName (direct array access)
                row.createCell(2).setCellValue(LAST_NAMES[random.nextInt(LAST_NAMES.length)]);
                
                // DOB (direct array access)
                row.createCell(3).setCellValue(DATE_PATTERNS[random.nextInt(DATE_PATTERNS.length)]);
                
                // class (direct array access)
                row.createCell(4).setCellValue(CLASSES[random.nextInt(CLASSES.length)]);
                
                // score (direct calculation)
                row.createCell(5).setCellValue(55 + random.nextInt(21));
                
                // Progress logging for large datasets
                if (i % 100000 == 0) {
                    System.out.println("Generated " + i + " records...");
                }
            }

            // Skip auto-sizing for large files (performance killer)
            System.out.println("Writing Excel file with " + numberOfRecords + " records...");
            
            // Save workbook with buffered output for better performance
            try (FileOutputStream fileOut = new FileOutputStream(filePath.toFile());
                 BufferedOutputStream bufferedOut = new BufferedOutputStream(fileOut, 65536)) {
                workbook.write(bufferedOut);
            }
        }

        System.out.println("Excel file generated: " + filePath.toString());
        return filePath.toString();
    }

    // Optimized method for performance testing with smaller datasets
    public String generateExcelFileFast(int numberOfRecords) throws IOException {
        // Create storage directory
        String storagePath = getStoragePath();
        Path excelPath = Paths.get(storagePath, excelFolder);
        Files.createDirectories(excelPath);

        // Generate filename
        String fileName = "students_fast_" + System.currentTimeMillis() + ".xlsx";
        Path filePath = excelPath.resolve(fileName);

        // Use SXSSFWorkbook with minimal memory footprint
        try (SXSSFWorkbook workbook = new SXSSFWorkbook(50)) { // Keep only 50 rows in memory
            Sheet sheet = workbook.createSheet("Students");

            // Create header row
            Row headerRow = sheet.createRow(0);
            String[] headers = {"studentId", "firstName", "lastName", "DOB", "class", "score"};
            for (int i = 0; i < headers.length; i++) {
                Cell cell = headerRow.createCell(i);
                cell.setCellValue(headers[i]);
            }

            // Use ThreadLocalRandom for better performance
            ThreadLocalRandom random = ThreadLocalRandom.current();
            
            // Generate data rows with minimal object creation
            for (int i = 1; i <= numberOfRecords; i++) {
                Row row = sheet.createRow(i);
                
                // studentId
                row.createCell(0).setCellValue(i);
                
                // firstName (from pre-defined array)
                row.createCell(1).setCellValue(FIRST_NAMES[random.nextInt(FIRST_NAMES.length)]);
                
                // lastName (from pre-defined array)
                row.createCell(2).setCellValue(LAST_NAMES[random.nextInt(LAST_NAMES.length)]);
                
                // DOB (from pre-defined array)
                row.createCell(3).setCellValue(DATE_PATTERNS[random.nextInt(DATE_PATTERNS.length)]);
                
                // class (from pre-defined array)
                row.createCell(4).setCellValue(CLASSES[random.nextInt(CLASSES.length)]);
                
                // score
                row.createCell(5).setCellValue(55 + random.nextInt(21));
            }

            // Skip auto-sizing for performance
            // Save workbook
            try (FileOutputStream fileOut = new FileOutputStream(filePath.toFile())) {
                workbook.write(fileOut);
            }
        }

        return filePath.toString();
    }

    public String getStoragePath() {
        String os = System.getProperty("os.name").toLowerCase();
        if (os.contains("win")) {
            return windowsStoragePath;
        } else {
            return linuxStoragePath;
        }
    }
}
