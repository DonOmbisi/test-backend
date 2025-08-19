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
        if (originalFilename == null || originalFilename.trim().isEmpty()) {
            originalFilename = "uploaded_file.xlsx";
        }
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
                    
                    // Read student data from Excel with proper cell type checking
                    Cell idCell = row.getCell(0);
                    if (idCell != null) {
                        if (idCell.getCellType() == CellType.NUMERIC) {
                            student.setStudentId((long) idCell.getNumericCellValue());
                        } else {
                            student.setStudentId(Long.parseLong(idCell.getStringCellValue()));
                        }
                    }
                    
                    Cell firstNameCell = row.getCell(1);
                    if (firstNameCell != null) {
                        if (firstNameCell.getCellType() == CellType.STRING) {
                            student.setFirstName(firstNameCell.getStringCellValue());
                        } else {
                            student.setFirstName(String.valueOf(firstNameCell.getNumericCellValue()));
                        }
                    }
                    
                    Cell lastNameCell = row.getCell(2);
                    if (lastNameCell != null) {
                        if (lastNameCell.getCellType() == CellType.STRING) {
                            student.setLastName(lastNameCell.getStringCellValue());
                        } else {
                            student.setLastName(String.valueOf(lastNameCell.getNumericCellValue()));
                        }
                    }
                    
                    // Handle date cell
                    Cell dateCell = row.getCell(3);
                    if (dateCell != null) {
                        try {
                            if (dateCell.getCellType() == CellType.NUMERIC) {
                                student.setDob(dateCell.getLocalDateTimeCellValue().toLocalDate().toString());
                            } else {
                                // Parse string date with multiple format attempts
                                String dateStr = dateCell.getStringCellValue();
                                LocalDate parsedDate = null;
                                
                                // Try different date formats
                                String[] dateFormats = {"yyyy-MM-dd", "dd/MM/yyyy", "MM/dd/yyyy", "dd-MM-yyyy", "MM-dd-yyyy"};
                                for (String format : dateFormats) {
                                    try {
                                        parsedDate = LocalDate.parse(dateStr, DateTimeFormatter.ofPattern(format));
                                        break;
                                    } catch (Exception e) {
                                        // Continue to next format
                                    }
                                }
                                
                                if (parsedDate != null) {
                                    student.setDob(parsedDate.toString());
                                } else {
                                    // If no date format works, use a default date
                                    student.setDob(LocalDate.now().toString());
                                }
                            }
                        } catch (Exception e) {
                            // If date parsing fails, use a default date
                            student.setDob(LocalDate.now().toString());
                        }
                    } else {
                        // If no date cell, use a default date
                        student.setDob(LocalDate.now().toString());
                    }
                    
                    Cell classNameCell = row.getCell(4);
                    if (classNameCell != null) {
                        if (classNameCell.getCellType() == CellType.STRING) {
                            student.setClassName(classNameCell.getStringCellValue());
                        } else {
                            student.setClassName(String.valueOf(classNameCell.getNumericCellValue()));
                        }
                    }
                    
                    // Update score by +10
                    Cell scoreCell = row.getCell(5);
                    if (scoreCell != null) {
                        int originalScore;
                        if (scoreCell.getCellType() == CellType.NUMERIC) {
                            originalScore = (int) scoreCell.getNumericCellValue();
                        } else {
                            originalScore = Integer.parseInt(scoreCell.getStringCellValue());
                        }
                        student.setScore(originalScore + 10);
                    }
                    
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
                    student.getDob(),
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
