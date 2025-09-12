@echo off
echo Starting Student Data Processor with optimized JVM settings for 1M records...

REM JVM Memory Settings - Optimized for 1M records processing
set JAVA_OPTS=-Xms4g -Xmx12g -XX:NewRatio=2 -XX:SurvivorRatio=6

REM Unlock Experimental Options First
set JAVA_OPTS=%JAVA_OPTS% -XX:+UnlockExperimentalVMOptions

REM Garbage Collection Optimization - G1GC for large datasets with connection pool management
set JAVA_OPTS=%JAVA_OPTS% -XX:+UseG1GC -XX:MaxGCPauseMillis=100 -XX:G1HeapRegionSize=32m
set JAVA_OPTS=%JAVA_OPTS% -XX:G1MixedGCCountTarget=8 -XX:G1OldCSetRegionThresholdPercent=10

REM Performance Tuning
set JAVA_OPTS=%JAVA_OPTS% -XX:+UseStringDeduplication -XX:+OptimizeStringConcat
set JAVA_OPTS=%JAVA_OPTS% -XX:+UseCompressedOops -XX:+UseCompressedClassPointers

REM JIT Compiler Optimization
set JAVA_OPTS=%JAVA_OPTS% -XX:+TieredCompilation -XX:TieredStopAtLevel=4
set JAVA_OPTS=%JAVA_OPTS% -XX:CompileThreshold=10000

REM I/O and Network Optimization
set JAVA_OPTS=%JAVA_OPTS% -Djava.awt.headless=true
set JAVA_OPTS=%JAVA_OPTS% -Dfile.encoding=UTF-8
set JAVA_OPTS=%JAVA_OPTS% -Djava.net.preferIPv4Stack=true

REM Large File Processing Optimization
set JAVA_OPTS=%JAVA_OPTS% -XX:+UseLargePages
set JAVA_OPTS=%JAVA_OPTS% -XX:LargePageSizeInBytes=2m

REM Database Connection Pool Optimization
set JAVA_OPTS=%JAVA_OPTS% -Dhikari.maximumPoolSize=30
set JAVA_OPTS=%JAVA_OPTS% -Dhikari.leakDetectionThreshold=30000

REM Monitoring and Debugging (optional - comment out for production)
REM set JAVA_OPTS=%JAVA_OPTS% -XX:+PrintGC -XX:+PrintGCDetails -XX:+PrintGCTimeStamps
REM set JAVA_OPTS=%JAVA_OPTS% -Xloggc:gc.log -XX:+UseGCLogFileRotation -XX:NumberOfGCLogFiles=5 -XX:GCLogFileSize=10M

echo JVM Options: %JAVA_OPTS%
echo.

REM Start the application
mvn spring-boot:run -Dspring-boot.run.jvmArguments="%JAVA_OPTS%"

pause
