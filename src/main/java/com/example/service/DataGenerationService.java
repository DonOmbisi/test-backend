package com.example.service;

import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

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

        // Use SXSSFWorkbook for better memory management and performance
        try (SXSSFWorkbook workbook = new SXSSFWorkbook(100)) { // Keep 100 rows in memory
            Sheet sheet = workbook.createSheet("Students");

            // Create header row
            Row headerRow = sheet.createRow(0);
            String[] headers = {"studentId", "firstName", "lastName", "DOB", "class", "score"};
            for (int i = 0; i < headers.length; i++) {
                Cell cell = headerRow.createCell(i);
                cell.setCellValue(headers[i]);
            }

            // Pre-generate random data arrays for better performance
            String[] randomFirstNames = new String[numberOfRecords];
            String[] randomLastNames = new String[numberOfRecords];
            String[] randomDates = new String[numberOfRecords];
            String[] randomClasses = new String[numberOfRecords];
            int[] randomScores = new int[numberOfRecords];
            
            // Fill arrays with random data
            ThreadLocalRandom random = ThreadLocalRandom.current();
            for (int i = 0; i < numberOfRecords; i++) {
                randomFirstNames[i] = FIRST_NAMES[random.nextInt(FIRST_NAMES.length)];
                randomLastNames[i] = LAST_NAMES[random.nextInt(LAST_NAMES.length)];
                randomDates[i] = DATE_PATTERNS[random.nextInt(DATE_PATTERNS.length)];
                randomClasses[i] = CLASSES[random.nextInt(CLASSES.length)];
                randomScores[i] = 55 + random.nextInt(21);
            }

            // Generate data rows in batches for better performance
            int batchSize = 1000;
            for (int batch = 0; batch < numberOfRecords; batch += batchSize) {
                int endIndex = Math.min(batch + batchSize, numberOfRecords);
                
                for (int i = batch; i < endIndex; i++) {
                    Row row = sheet.createRow(i + 1); // +1 for header
                    
                    // studentId (starting from 1)
                    row.createCell(0).setCellValue(i + 1);
                    
                    // firstName (from pre-generated array)
                    row.createCell(1).setCellValue(randomFirstNames[i]);
                    
                    // lastName (from pre-generated array)
                    row.createCell(2).setCellValue(randomLastNames[i]);
                    
                    // DOB (from pre-generated array)
                    row.createCell(3).setCellValue(randomDates[i]);
                    
                    // class (from pre-generated array)
                    row.createCell(4).setCellValue(randomClasses[i]);
                    
                    // score (from pre-generated array)
                    row.createCell(5).setCellValue(randomScores[i]);
                }
            }

            // Auto-size columns (only for smaller files to avoid performance impact)
            if (numberOfRecords <= 10000) {
                // For SXSSFWorkbook, we need to track columns before auto-sizing
                if (sheet instanceof org.apache.poi.xssf.streaming.SXSSFSheet) {
                    ((org.apache.poi.xssf.streaming.SXSSFSheet) sheet).trackAllColumnsForAutoSizing();
                }
                for (int i = 0; i < headers.length; i++) {
                    sheet.autoSizeColumn(i);
                }
            }

            // Save workbook
            try (FileOutputStream fileOut = new FileOutputStream(filePath.toFile())) {
                workbook.write(fileOut);
            }
        }

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
