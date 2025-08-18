package com.example.entity;

import jakarta.persistence.*;
import java.time.LocalDate;

@Entity
@Table(name = "students")
public class Student {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "studentId")
    private Long studentId;

    @Column(name = "firstName", nullable = false, length = 50)
    private String firstName;

    @Column(name = "lastName", nullable = false, length = 50)
    private String lastName;

    @Column(name = "DOB", nullable = false)
    private LocalDate dob;

    @Column(name = "class", nullable = false, length = 20)
    private String className;

    @Column(name = "score", nullable = false)
    private Integer score;

    // Default constructor
    public Student() {}

    // Constructor with all fields
    public Student(Long studentId, String firstName, String lastName, LocalDate dob, String className, Integer score) {
        this.studentId = studentId;
        this.firstName = firstName;
        this.lastName = lastName;
        this.dob = dob;
        this.className = className;
        this.score = score;
    }

    // Getters and Setters
    public Long getStudentId() {
        return studentId;
    }

    public void setStudentId(Long studentId) {
        this.studentId = studentId;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public LocalDate getDob() {
        return dob;
    }

    public void setDob(LocalDate dob) {
        this.dob = dob;
    }

    public String getClassName() {
        return className;
    }

    public void setClassName(String className) {
        this.className = className;
    }

    public Integer getScore() {
        return score;
    }

    public void setScore(Integer score) {
        this.score = score;
    }

    @Override
    public String toString() {
        return "Student{" +
                "studentId=" + studentId +
                ", firstName='" + firstName + '\'' +
                ", lastName='" + lastName + '\'' +
                ", dob=" + dob +
                ", className='" + className + '\'' +
                ", score=" + score +
                '}';
    }
}
