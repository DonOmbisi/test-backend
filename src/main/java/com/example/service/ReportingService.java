package com.example.service;

import com.example.dto.ReportRequest;
import com.example.dto.StudentDto;
import com.example.entity.Student;
import com.example.repository.StudentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Service
public class ReportingService {

    @Autowired
    private StudentRepository studentRepository;

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
        Long count = studentRepository.countByFilters(
            request.getStudentId(), 
            request.getClassName()
        );
        return count != null ? count : 0L;
    }

    public String exportToExcel(ReportRequest request) throws IOException {
        // For now, we'll create a simple CSV file as Excel
        // In a real implementation, you would use Apache POI to create Excel files
        String fileName = "report_" + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss")) + ".xlsx";
        Path reportsDir = Paths.get("reports");
        if (!Files.exists(reportsDir)) {
            Files.createDirectories(reportsDir);
        }
        
        Path filePath = reportsDir.resolve(fileName);
        
        // Create a simple CSV file for now
        try (FileWriter writer = new FileWriter(filePath.toFile())) {
            writer.write("Student ID,First Name,Last Name,Date of Birth,Class,Score\n");
            
            Page<StudentDto> students = getStudents(request);
            for (StudentDto student : students.getContent()) {
                writer.write(String.format("%d,%s,%s,%s,%s,%d\n",
                    student.getStudentId(),
                    student.getFirstName(),
                    student.getLastName(),
                    student.getDob(),
                    student.getClassName(),
                    student.getScore()));
            }
        }
        
        return filePath.toString();
    }

    public String exportToCsv(ReportRequest request) throws IOException {
        String fileName = "report_" + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss")) + ".csv";
        Path reportsDir = Paths.get("reports");
        if (!Files.exists(reportsDir)) {
            Files.createDirectories(reportsDir);
        }
        
        Path filePath = reportsDir.resolve(fileName);
        
        try (FileWriter writer = new FileWriter(filePath.toFile())) {
            writer.write("Student ID,First Name,Last Name,Date of Birth,Class,Score\n");
            
            Page<StudentDto> students = getStudents(request);
            for (StudentDto student : students.getContent()) {
                writer.write(String.format("%d,%s,%s,%s,%s,%d\n",
                    student.getStudentId(),
                    student.getFirstName(),
                    student.getLastName(),
                    student.getDob(),
                    student.getClassName(),
                    student.getScore()));
            }
        }
        
        return filePath.toString();
    }

    public String exportToPdf(ReportRequest request) throws IOException {
        // For now, we'll create a simple text file as PDF
        // In a real implementation, you would use a library like iText to create PDF files
        String fileName = "report_" + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss")) + ".pdf";
        Path reportsDir = Paths.get("reports");
        if (!Files.exists(reportsDir)) {
            Files.createDirectories(reportsDir);
        }
        
        Path filePath = reportsDir.resolve(fileName);
        
        try (FileWriter writer = new FileWriter(filePath.toFile())) {
            writer.write("STUDENT REPORT\n");
            writer.write("Generated on: " + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")) + "\n\n");
            
            Page<StudentDto> students = getStudents(request);
            for (StudentDto student : students.getContent()) {
                writer.write(String.format("ID: %d | Name: %s %s | DOB: %s | Class: %s | Score: %d\n",
                    student.getStudentId(),
                    student.getFirstName(),
                    student.getLastName(),
                    student.getDob(),
                    student.getClassName(),
                    student.getScore()));
            }
        }
        
        return filePath.toString();
    }

    private StudentDto convertToDto(Student student) {
        StudentDto dto = new StudentDto();
        dto.setStudentId(student.getStudentId());
        dto.setFirstName(student.getFirstName());
        dto.setLastName(student.getLastName());
        dto.setDob(student.getDob().toString());
        dto.setClassName(student.getClassName());
        dto.setScore(student.getScore());
        return dto;
    }
}
