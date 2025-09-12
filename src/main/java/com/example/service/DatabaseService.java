package com.example.service;

import com.example.entity.Student;
import com.example.repository.StudentRepository;
import com.opencsv.CSVReader;
import com.opencsv.exceptions.CsvValidationException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStreamReader;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;

@Service
public class DatabaseService {

    @Autowired
    private StudentRepository studentRepository;

    public int uploadCsvToDatabase(MultipartFile csvFile) throws IOException, CsvValidationException {
        int totalSaved = 0;
        int batchSize = 5000; // Optimized batch size for 1M records
        List<Student> batch = new ArrayList<>(batchSize);
        int totalLines = 0;
        int errorLines = 0;
        
        // Disable auto-commit for better performance
        System.out.println("Starting optimized CSV to database upload...");

        try (CSVReader reader = new CSVReader(new InputStreamReader(csvFile.getInputStream()))) {
            // Skip header row
            String[] header = reader.readNext();
            System.out.println("CSV Header: " + String.join(",", header));
            System.out.println("Detected columns: " + header.length);
            
            if (header.length == 6) {
                System.out.println("Expected format: studentId, firstName, lastName, DOB, className, score");
            } else if (header.length >= 7) {
                System.out.println("Detected format: studentId, firstName, lastName, age, email, className, score, year");
                System.out.println("Note: Converting age to approximate birth year, ignoring email and year fields");
            } else {
                System.out.println("Warning: Unexpected column count. Expected 6 or 7+ columns.");
            }
            
            String[] line;
            while ((line = reader.readNext()) != null) {
                totalLines++;
                
                if (line.length >= 7) { // Expect 7 columns: id, firstName, lastName, age, email, className, score, year
                    try {
                        Student student = new Student();
                        
                        // Parse student data from CSV (skip studentId - let database auto-generate)
                        // student.setStudentId(Long.parseLong(line[0])); // Skip this - let DB auto-generate
                        student.setFirstName(line[1]);
                        student.setLastName(line[2]);
                        
                        // Handle age field (convert to approximate birth year)
                        int age = Integer.parseInt(line[3]);
                        int currentYear = LocalDate.now().getYear();
                        int birthYear = currentYear - age;
                        student.setDob(LocalDate.of(birthYear, 1, 1)); // Use January 1st as default
                        
                        student.setClassName(line[5]); // className is at index 5
                        
                        // Update score by +5 (as per requirements: CSV score + 5 = Database score)
                        int originalScore = Integer.parseInt(line[6]); // score is at index 6
                        student.setScore(originalScore + 5);
                        
                        batch.add(student);
                        
                        // Save batch when it reaches the batch size
                        if (batch.size() >= batchSize) {
                            totalSaved += saveBatchOptimized(batch);
                            batch.clear();
                            
                            // Force garbage collection every 10 batches to prevent memory buildup
                            if ((totalSaved / batchSize) % 10 == 0) {
                                System.gc();
                            }
                            
                            // Progress logging
                            if (totalLines % 100000 == 0) {
                                System.out.println("Processed " + totalLines + " lines, saved " + totalSaved + " records...");
                            }
                        }
                    } catch (NumberFormatException | DateTimeParseException e) {
                        // Log error but continue processing other records
                        errorLines++;
                        System.err.println("Error parsing line " + totalLines + ": " + String.join(",", line) + " - " + e.getMessage());
                        continue;
                    }
                } else if (line.length >= 6) {
                    // Fallback for 6-column format (id, firstName, lastName, dob, className, score)
                    try {
                        Student student = new Student();
                        
                        // student.setStudentId(Long.parseLong(line[0])); // Skip this - let DB auto-generate
                        student.setFirstName(line[1]);
                        student.setLastName(line[2]);
                        student.setDob(LocalDate.parse(line[3], DateTimeFormatter.ofPattern("yyyy-MM-dd")));
                        student.setClassName(line[4]);
                        
                        int originalScore = Integer.parseInt(line[5]);
                        student.setScore(originalScore + 5);
                        
                        batch.add(student);
                        
                        if (batch.size() >= batchSize) {
                            totalSaved += saveBatchOptimized(batch);
                            batch.clear();
                            
                            // Force garbage collection every 10 batches to prevent memory buildup
                            if ((totalSaved / batchSize) % 10 == 0) {
                                System.gc();
                            }
                        }
                    } catch (NumberFormatException | DateTimeParseException e) {
                        errorLines++;
                        System.err.println("Error parsing line " + totalLines + ": " + String.join(",", line) + " - " + e.getMessage());
                        continue;
                    }
                } else {
                    errorLines++;
                    System.err.println("Skipping line " + totalLines + " (insufficient columns): " + String.join(",", line));
                }
            }
            
            // Save remaining records in the last batch
            if (!batch.isEmpty()) {
                totalSaved += saveBatchOptimized(batch);
            }
        }

        System.out.println("CSV Processing Summary:");
        System.out.println("Total lines processed: " + totalLines);
        System.out.println("Lines with errors: " + errorLines);
        System.out.println("Successfully saved: " + totalSaved);
        System.out.println("Success rate: " + String.format("%.2f%%", (double)(totalLines - errorLines) / totalLines * 100));

        return totalSaved;
    }

    public long getTotalStudentCount() {
        return studentRepository.count();
    }

    public List<Student> getAllStudents() {
        return studentRepository.findAll();
    }
    
    // Optimized batch saving method with proper transaction management
    @Transactional
    public int saveBatchOptimized(List<Student> batch) {
        try {
            // Use saveAllAndFlush for immediate persistence
            List<Student> savedStudents = studentRepository.saveAllAndFlush(batch);
            return savedStudents.size();
        } catch (Exception e) {
            System.err.println("Batch save failed: " + e.getMessage());
            // Don't attempt individual saves in case of batch failure
            // This prevents connection issues
            return 0;
        }
    }
}
