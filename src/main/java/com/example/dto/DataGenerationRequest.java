package com.example.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Max;

public class DataGenerationRequest {
    
    @Min(value = 1, message = "Number of records must be at least 1")
    @Max(value = 1000000, message = "Number of records cannot exceed 1,000,000")
    private Integer numberOfRecords;

    public DataGenerationRequest() {}

    public DataGenerationRequest(Integer numberOfRecords) {
        this.numberOfRecords = numberOfRecords;
    }

    public Integer getNumberOfRecords() {
        return numberOfRecords;
    }

    public void setNumberOfRecords(Integer numberOfRecords) {
        this.numberOfRecords = numberOfRecords;
    }
}
