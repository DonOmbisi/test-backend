package com.example.dto;

public class ReportRequest {
    private Integer page;
    private Integer size;
    private Long studentId;
    private String className;
    
    // Validation method
    public boolean isValid() {
        if (page != null && page < 0) return false;
        if (size != null && size <= 0) return false;
        if (studentId != null && studentId <= 0) return false;
        return true;
    }
    
    public String getValidationErrors() {
        if (page != null && page < 0) return "Page must be non-negative";
        if (size != null && size <= 0) return "Size must be positive";
        if (studentId != null && studentId <= 0) return "Student ID must be positive";
        return null;
    }

    public ReportRequest() {}

    public ReportRequest(Integer page, Integer size, Long studentId, String className) {
        this.page = page;
        this.size = size;
        this.studentId = studentId;
        this.className = className;
    }

    public Integer getPage() {
        return page;
    }

    public void setPage(Integer page) {
        this.page = page;
    }

    public Integer getSize() {
        return size;
    }

    public void setSize(Integer size) {
        this.size = size;
    }

    public Long getStudentId() {
        return studentId;
    }

    public void setStudentId(Long studentId) {
        this.studentId = studentId;
    }

    public String getClassName() {
        return className;
    }

    public void setClassName(String className) {
        this.className = className;
    }
}
