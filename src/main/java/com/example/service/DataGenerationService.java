package com.example.service;

import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Random;

@Service
public class DataGenerationService {

    @Value("${app.file.storage.windows}")
    private String windowsStoragePath;

    @Value("${app.file.storage.linux}")
    private String linuxStoragePath;

    @Value("${app.file.storage.excel}")
    private String excelFolder;

    private static final String[] CLASSES = {"Class1", "Class2", "Class3", "Class4", "Class5"};

    private final Random random = new Random();

    public String generateExcelFile(int numberOfRecords) throws IOException {
        // Create storage directory
        String storagePath = getStoragePath();
        Path excelPath = Paths.get(storagePath, excelFolder);
        Files.createDirectories(excelPath);

        // Generate filename
        String fileName = "students_" + System.currentTimeMillis() + ".xlsx";
        Path filePath = excelPath.resolve(fileName);

        // Create workbook and sheet
        try (Workbook workbook = new XSSFWorkbook()) {
            Sheet sheet = workbook.createSheet("Students");

            // Create header row
            Row headerRow = sheet.createRow(0);
            String[] headers = {"studentId", "firstName", "lastName", "DOB", "class", "score"};
            for (int i = 0; i < headers.length; i++) {
                Cell cell = headerRow.createCell(i);
                cell.setCellValue(headers[i]);
            }

            // Generate data rows
            for (int i = 1; i <= numberOfRecords; i++) {
                Row row = sheet.createRow(i);
                
                // studentId (starting from 1)
                row.createCell(0).setCellValue(i);
                
                // firstName (random 3-8 characters)
                row.createCell(1).setCellValue(generateRandomString(3, 8));
                
                // lastName (random 3-8 characters)
                row.createCell(2).setCellValue(generateRandomString(3, 8));
                
                // DOB (random date between 2000-2010)
                row.createCell(3).setCellValue(generateRandomDate());
                
                // class (random from Class1-Class5)
                row.createCell(4).setCellValue(CLASSES[random.nextInt(CLASSES.length)]);
                
                // score (random 55-75)
                row.createCell(5).setCellValue(55 + random.nextInt(21));
            }

            // Auto-size columns
            for (int i = 0; i < headers.length; i++) {
                sheet.autoSizeColumn(i);
            }

            // Save workbook
            try (FileOutputStream fileOut = new FileOutputStream(filePath.toFile())) {
                workbook.write(fileOut);
            }
        }

        return filePath.toString();
    }

    private String generateRandomString(int minLength, int maxLength) {
        int length = minLength + random.nextInt(maxLength - minLength + 1);
        StringBuilder sb = new StringBuilder();
        String chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz";
        
        for (int i = 0; i < length; i++) {
            sb.append(chars.charAt(random.nextInt(chars.length())));
        }
        
        return sb.toString();
    }

    private String generateRandomDate() {
        LocalDate startDate = LocalDate.of(2000, 1, 1);
        LocalDate endDate = LocalDate.of(2010, 12, 31);
        
        long startEpochDay = startDate.toEpochDay();
        long endEpochDay = endDate.toEpochDay();
        long randomEpochDay = startEpochDay + random.nextInt((int) (endEpochDay - startEpochDay + 1));
        
        LocalDate randomDate = LocalDate.ofEpochDay(randomEpochDay);
        return randomDate.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
    }

    private String getStoragePath() {
        String os = System.getProperty("os.name").toLowerCase();
        if (os.contains("win")) {
            return windowsStoragePath;
        } else {
            return linuxStoragePath;
        }
    }
}
