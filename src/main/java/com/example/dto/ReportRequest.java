package com.example.dto;

public class ReportRequest {
    private Integer page = 0;
    private Integer size = 20;
    private Long studentId;
    private String className;

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
