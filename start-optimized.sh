#!/bin/bash

echo "Starting Student Data Processor with optimized JVM settings for 1M records..."

# JVM Memory Settings - Optimized for 1M records processing
JAVA_OPTS="-Xms2g -Xmx8g -XX:NewRatio=1 -XX:SurvivorRatio=8"

# Garbage Collection Optimization - G1GC for large datasets
JAVA_OPTS="$JAVA_OPTS -XX:+UseG1GC -XX:MaxGCPauseMillis=200 -XX:G1HeapRegionSize=16m"

# Performance Tuning
JAVA_OPTS="$JAVA_OPTS -XX:+UseStringDeduplication -XX:+OptimizeStringConcat"
JAVA_OPTS="$JAVA_OPTS -XX:+UseCompressedOops -XX:+UseCompressedClassPointers"

# JIT Compiler Optimization
JAVA_OPTS="$JAVA_OPTS -XX:+TieredCompilation -XX:TieredStopAtLevel=4"
JAVA_OPTS="$JAVA_OPTS -XX:CompileThreshold=10000"

# I/O and Network Optimization
JAVA_OPTS="$JAVA_OPTS -Djava.awt.headless=true"
JAVA_OPTS="$JAVA_OPTS -Dfile.encoding=UTF-8"
JAVA_OPTS="$JAVA_OPTS -Djava.net.preferIPv4Stack=true"

# Large File Processing Optimization
JAVA_OPTS="$JAVA_OPTS -XX:+UnlockExperimentalVMOptions"
JAVA_OPTS="$JAVA_OPTS -XX:+UseLargePages"
JAVA_OPTS="$JAVA_OPTS -XX:LargePageSizeInBytes=2m"

# Monitoring and Debugging (optional - comment out for production)
# JAVA_OPTS="$JAVA_OPTS -XX:+PrintGC -XX:+PrintGCDetails -XX:+PrintGCTimeStamps"
# JAVA_OPTS="$JAVA_OPTS -Xloggc:gc.log -XX:+UseGCLogFileRotation -XX:NumberOfGCLogFiles=5 -XX:GCLogFileSize=10M"

echo "JVM Options: $JAVA_OPTS"
echo

# Start the application
mvn spring-boot:run -Dspring-boot.run.jvmArguments="$JAVA_OPTS"
