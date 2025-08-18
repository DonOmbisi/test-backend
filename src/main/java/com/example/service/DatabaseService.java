package com.example.service;

import com.example.entity.Student;
import com.example.repository.StudentRepository;
import com.opencsv.CSVReader;
import com.opencsv.exceptions.CsvValidationException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
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
        int batchSize = 1000;
        List<Student> batch = new ArrayList<>(batchSize);

        try (CSVReader reader = new CSVReader(new InputStreamReader(csvFile.getInputStream()))) {
            // Skip header row
            reader.readNext();
            
            String[] line;
            while ((line = reader.readNext()) != null) {
                if (line.length >= 6) {
                    try {
                        Student student = new Student();
                        
                        // Parse student data from CSV
                        student.setStudentId(Long.parseLong(line[0]));
                        student.setFirstName(line[1]);
                        student.setLastName(line[2]);
                        student.setDob(LocalDate.parse(line[3], DateTimeFormatter.ofPattern("yyyy-MM-dd")));
                        student.setClassName(line[4]);
                        
                        // Update score by +5 (as per requirements: CSV score + 5 = Database score)
                        int originalScore = Integer.parseInt(line[5]);
                        student.setScore(originalScore + 5);
                        
                        batch.add(student);
                        
                        // Save batch when it reaches the batch size
                        if (batch.size() >= batchSize) {
                            studentRepository.saveAll(batch);
                            totalSaved += batch.size();
                            batch.clear();
                        }
                    } catch (NumberFormatException | DateTimeParseException e) {
                        // Log error but continue processing other records
                        System.err.println("Error parsing line: " + String.join(",", line) + " - " + e.getMessage());
                        continue;
                    }
                }
            }
            
            // Save remaining records in the last batch
            if (!batch.isEmpty()) {
                studentRepository.saveAll(batch);
                totalSaved += batch.size();
            }
        }

        return totalSaved;
    }

    public long getTotalStudentCount() {
        return studentRepository.count();
    }

    public List<Student> getAllStudents() {
        return studentRepository.findAll();
    }
}
