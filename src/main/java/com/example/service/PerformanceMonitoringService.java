package com.example.service;

import org.springframework.stereotype.Service;

import java.lang.management.ManagementFactory;
import java.lang.management.MemoryMXBean;
import java.lang.management.MemoryUsage;
import java.lang.management.RuntimeMXBean;
import java.util.HashMap;
import java.util.Map;

@Service
public class PerformanceMonitoringService {

    private final MemoryMXBean memoryBean = ManagementFactory.getMemoryMXBean();
    private final RuntimeMXBean runtimeBean = ManagementFactory.getRuntimeMXBean();

    public Map<String, Object> getPerformanceMetrics() {
        Map<String, Object> metrics = new HashMap<>();
        
        // Memory metrics
        MemoryUsage heapMemory = memoryBean.getHeapMemoryUsage();
        MemoryUsage nonHeapMemory = memoryBean.getNonHeapMemoryUsage();
        
        Map<String, Object> memoryMetrics = new HashMap<>();
        memoryMetrics.put("heapUsed", formatBytes(heapMemory.getUsed()));
        memoryMetrics.put("heapMax", formatBytes(heapMemory.getMax()));
        memoryMetrics.put("heapUsedPercentage", String.format("%.2f%%", (double) heapMemory.getUsed() / heapMemory.getMax() * 100));
        memoryMetrics.put("nonHeapUsed", formatBytes(nonHeapMemory.getUsed()));
        
        metrics.put("memory", memoryMetrics);
        
        // Runtime metrics
        Map<String, Object> runtimeMetrics = new HashMap<>();
        runtimeMetrics.put("uptime", formatDuration(runtimeBean.getUptime()));
        runtimeMetrics.put("jvmName", runtimeBean.getVmName());
        runtimeMetrics.put("jvmVersion", runtimeBean.getVmVersion());
        runtimeMetrics.put("availableProcessors", Runtime.getRuntime().availableProcessors());
        
        metrics.put("runtime", runtimeMetrics);
        
        // System metrics
        Map<String, Object> systemMetrics = new HashMap<>();
        systemMetrics.put("totalMemory", formatBytes(Runtime.getRuntime().totalMemory()));
        systemMetrics.put("freeMemory", formatBytes(Runtime.getRuntime().freeMemory()));
        systemMetrics.put("maxMemory", formatBytes(Runtime.getRuntime().maxMemory()));
        
        metrics.put("system", systemMetrics);
        
        return metrics;
    }
    
    public Map<String, Object> getOptimizationRecommendations() {
        Map<String, Object> recommendations = new HashMap<>();
        
        MemoryUsage heapMemory = memoryBean.getHeapMemoryUsage();
        double heapUsagePercentage = (double) heapMemory.getUsed() / heapMemory.getMax() * 100;
        
        if (heapUsagePercentage > 85) {
            recommendations.put("memory", "High memory usage detected. Consider increasing heap size or optimizing data processing.");
        } else if (heapUsagePercentage < 30) {
            recommendations.put("memory", "Low memory usage. You can potentially reduce heap size for better performance.");
        } else {
            recommendations.put("memory", "Memory usage is optimal.");
        }
        
        // Performance tips for 1M records
        recommendations.put("largeDataProcessing", "For 1M+ records: Use streaming processing, increase batch sizes, and ensure adequate heap memory (8GB+ recommended).");
        
        return recommendations;
    }
    
    private String formatBytes(long bytes) {
        if (bytes < 1024) return bytes + " B";
        int exp = (int) (Math.log(bytes) / Math.log(1024));
        String pre = "KMGTPE".charAt(exp - 1) + "";
        return String.format("%.2f %sB", bytes / Math.pow(1024, exp), pre);
    }
    
    private String formatDuration(long millis) {
        long seconds = millis / 1000;
        long minutes = seconds / 60;
        long hours = minutes / 60;
        long days = hours / 24;
        
        if (days > 0) return String.format("%dd %dh %dm", days, hours % 24, minutes % 60);
        if (hours > 0) return String.format("%dh %dm %ds", hours, minutes % 60, seconds % 60);
        if (minutes > 0) return String.format("%dm %ds", minutes, seconds % 60);
        return String.format("%ds", seconds);
    }
}
