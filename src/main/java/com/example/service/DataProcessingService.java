package com.example.service;

import com.example.dto.StudentDto;
import com.opencsv.CSVWriter;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

@Service
public class DataProcessingService {

    @Value("${app.file.storage.windows}")
    private String windowsStoragePath;

    @Value("${app.file.storage.linux}")
    private String linuxStoragePath;

    @Value("${app.file.storage.csv}")
    private String csvFolder;

    public String convertExcelToCsv(MultipartFile excelFile) throws IOException {
        // Create storage directory
        String storagePath = getStoragePath();
        Path csvPath = Paths.get(storagePath, csvFolder);
        Files.createDirectories(csvPath);

        // Generate CSV filename
        String originalFilename = excelFile.getOriginalFilename();
        String csvFileName = originalFilename.replaceAll("\\.xlsx?$", ".csv");
        Path csvFilePath = csvPath.resolve(csvFileName);

        List<StudentDto> students = new ArrayList<>();

        // Read Excel file
        try (InputStream inputStream = excelFile.getInputStream();
             Workbook workbook = new XSSFWorkbook(inputStream)) {
            
            Sheet sheet = workbook.getSheetAt(0);
            
            // Skip header row, start from row 1
            for (int i = 1; i <= sheet.getLastRowNum(); i++) {
                Row row = sheet.getRow(i);
                if (row != null) {
                    StudentDto student = new StudentDto();
                    
                    // Read student data from Excel
                    student.setStudentId((long) row.getCell(0).getNumericCellValue());
                    student.setFirstName(row.getCell(1).getStringCellValue());
                    student.setLastName(row.getCell(2).getStringCellValue());
                    
                    // Handle date cell
                    Cell dateCell = row.getCell(3);
                    if (dateCell.getCellType() == CellType.NUMERIC) {
                        student.setDob(dateCell.getLocalDateTimeCellValue().toLocalDate());
                    } else {
                        // Parse string date
                        String dateStr = dateCell.getStringCellValue();
                        student.setDob(LocalDate.parse(dateStr, DateTimeFormatter.ofPattern("yyyy-MM-dd")));
                    }
                    
                    student.setClassName(row.getCell(4).getStringCellValue());
                    
                    // Update score by +10
                    int originalScore = (int) row.getCell(5).getNumericCellValue();
                    student.setScore(originalScore + 10);
                    
                    students.add(student);
                }
            }
        }

        // Write to CSV file
        try (CSVWriter writer = new CSVWriter(new FileWriter(csvFilePath.toFile()))) {
            // Write header
            String[] header = {"studentId", "firstName", "lastName", "DOB", "class", "score"};
            writer.writeNext(header);
            
            // Write data rows
            for (StudentDto student : students) {
                String[] row = {
                    String.valueOf(student.getStudentId()),
                    student.getFirstName(),
                    student.getLastName(),
                    student.getDob().format(DateTimeFormatter.ofPattern("yyyy-MM-dd")),
                    student.getClassName(),
                    String.valueOf(student.getScore())
                };
                writer.writeNext(row);
            }
        }

        return csvFilePath.toString();
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
