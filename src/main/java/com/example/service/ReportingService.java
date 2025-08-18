package com.example.service;

import com.example.dto.ReportRequest;
import com.example.dto.StudentDto;
import com.example.entity.Student;
import com.example.repository.StudentRepository;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.element.Table;
import com.itextpdf.layout.properties.TextAlignment;
import com.opencsv.CSVWriter;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

@Service
public class ReportingService {

    @Autowired
    private StudentRepository studentRepository;

    @Value("${app.file.storage.windows}")
    private String windowsStoragePath;

    @Value("${app.file.storage.linux}")
    private String linuxStoragePath;

    public Page<StudentDto> getStudents(ReportRequest request) {
        Pageable pageable = PageRequest.of(request.getPage(), request.getSize());
        
        Page<Student> studentsPage = studentRepository.findByFilters(
            request.getStudentId(), 
            request.getClassName(), 
            pageable
        );
        
        return studentsPage.map(this::convertToDto);
    }

    public long getTotalCount(ReportRequest request) {
        return studentRepository.countByFilters(request.getStudentId(), request.getClassName());
    }

    public String exportToExcel(ReportRequest request) throws IOException {
        String storagePath = getStoragePath();
        Path excelPath = Paths.get(storagePath, "reports");
        Files.createDirectories(excelPath);

        String fileName = "students_report_" + System.currentTimeMillis() + ".xlsx";
        Path filePath = excelPath.resolve(fileName);

        List<Student> students = getStudentsForExport(request);

        try (Workbook workbook = new XSSFWorkbook()) {
            Sheet sheet = workbook.createSheet("Students Report");

            // Create header row
            Row headerRow = sheet.createRow(0);
            String[] headers = {"Student ID", "First Name", "Last Name", "DOB", "Class", "Score"};
            for (int i = 0; i < headers.length; i++) {
                Cell cell = headerRow.createCell(i);
                cell.setCellValue(headers[i]);
            }

            // Create data rows
            int rowNum = 1;
            for (Student student : students) {
                Row row = sheet.createRow(rowNum++);
                row.createCell(0).setCellValue(student.getStudentId());
                row.createCell(1).setCellValue(student.getFirstName());
                row.createCell(2).setCellValue(student.getLastName());
                row.createCell(3).setCellValue(student.getDob().toString());
                row.createCell(4).setCellValue(student.getClassName());
                row.createCell(5).setCellValue(student.getScore());
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

    public String exportToCsv(ReportRequest request) throws IOException {
        String storagePath = getStoragePath();
        Path csvPath = Paths.get(storagePath, "reports");
        Files.createDirectories(csvPath);

        String fileName = "students_report_" + System.currentTimeMillis() + ".csv";
        Path filePath = csvPath.resolve(fileName);

        List<Student> students = getStudentsForExport(request);

        try (CSVWriter writer = new CSVWriter(new FileWriter(filePath.toFile()))) {
            // Write header
            String[] header = {"Student ID", "First Name", "Last Name", "DOB", "Class", "Score"};
            writer.writeNext(header);

            // Write data rows
            for (Student student : students) {
                String[] row = {
                    String.valueOf(student.getStudentId()),
                    student.getFirstName(),
                    student.getLastName(),
                    student.getDob().toString(),
                    student.getClassName(),
                    String.valueOf(student.getScore())
                };
                writer.writeNext(row);
            }
        }

        return filePath.toString();
    }

    public String exportToPdf(ReportRequest request) throws IOException {
        String storagePath = getStoragePath();
        Path pdfPath = Paths.get(storagePath, "reports");
        Files.createDirectories(pdfPath);

        String fileName = "students_report_" + System.currentTimeMillis() + ".pdf";
        Path filePath = pdfPath.resolve(fileName);

        List<Student> students = getStudentsForExport(request);

        try (PdfWriter writer = new PdfWriter(filePath.toFile());
             PdfDocument pdf = new PdfDocument(writer);
             Document document = new Document(pdf)) {

            // Add title
            Paragraph title = new Paragraph("Students Report");
            title.setTextAlignment(TextAlignment.CENTER);
            title.setFontSize(20);
            document.add(title);

            // Create table
            Table table = new Table(6);
            table.setWidth(500);

            // Add header
            table.addHeaderCell("Student ID");
            table.addHeaderCell("First Name");
            table.addHeaderCell("Last Name");
            table.addHeaderCell("DOB");
            table.addHeaderCell("Class");
            table.addHeaderCell("Score");

            // Add data rows
            for (Student student : students) {
                table.addCell(String.valueOf(student.getStudentId()));
                table.addCell(student.getFirstName());
                table.addCell(student.getLastName());
                table.addCell(student.getDob().toString());
                table.addCell(student.getClassName());
                table.addCell(String.valueOf(student.getScore()));
            }

            document.add(table);
        }

        return filePath.toString();
    }

    private List<Student> getStudentsForExport(ReportRequest request) {
        // For export, we get all matching records without pagination
        Pageable pageable = PageRequest.of(0, Integer.MAX_VALUE);
        Page<Student> studentsPage = studentRepository.findByFilters(
            request.getStudentId(), 
            request.getClassName(), 
            pageable
        );
        return studentsPage.getContent();
    }

    private StudentDto convertToDto(Student student) {
        return new StudentDto(
            student.getStudentId(),
            student.getFirstName(),
            student.getLastName(),
            student.getDob(),
            student.getClassName(),
            student.getScore()
        );
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
