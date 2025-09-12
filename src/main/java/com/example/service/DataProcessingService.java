package com.example.service;

import com.github.pjfanning.xlsx.StreamingReader;
import com.opencsv.CSVWriter;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.usermodel.DateUtil;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

@Service
public class DataProcessingService {

    @Value("${app.file.storage.windows}")
    private String windowsStoragePath;

    @Value("${app.file.storage.linux}")
    private String linuxStoragePath;

    @Value("${app.file.storage.csv}")
    private String csvFolder;

    public String convertExcelToCsv(MultipartFile excelFile) throws IOException {
        // Create storage directory
        String storagePath = getStoragePath();
        Path csvDir = Paths.get(storagePath, csvFolder);
        Files.createDirectories(csvDir);

        // Generate CSV filename
        String originalFilename = excelFile.getOriginalFilename();
        if (originalFilename == null || originalFilename.trim().isEmpty()) {
            originalFilename = "uploaded_file.xlsx";
        }
        String csvFileName = originalFilename.replaceAll("\\.xlsx?$", ".csv");
        Path csvFilePath = csvDir.resolve(csvFileName);

        // Precompile date formats
        DateTimeFormatter isoFormatter = DateTimeFormatter.ISO_LOCAL_DATE;
        DateTimeFormatter[] tryFormats = new DateTimeFormatter[]{
                DateTimeFormatter.ofPattern("dd/MM/yyyy"),
                DateTimeFormatter.ofPattern("MM/dd/yyyy"),
                DateTimeFormatter.ofPattern("dd-MM-yyyy")
        };
        DataFormatter dataFormatter = new DataFormatter();

        // Streaming read & write
        try (InputStream is = new BufferedInputStream(excelFile.getInputStream(), 64 * 1024);
             Workbook workbook = StreamingReader.builder()
                     .rowCacheSize(100)   // rows kept in memory
                     .bufferSize(4096)    // read buffer size
                     .open(is);
             BufferedWriter bw = Files.newBufferedWriter(csvFilePath);
             CSVWriter csvWriter = new CSVWriter(bw)) {

            Sheet sheet = workbook.getSheetAt(0);

            // Write header
            csvWriter.writeNext(new String[]{"studentId", "firstName", "lastName", "DOB", "class", "score"});

            int rowNum = 0;
            for (Row row : sheet) {
                // skip header row in Excel
                if (rowNum++ == 0) continue;

                String[] out = new String[6];

                // studentId
                out[0] = safeGetString(row, 0, dataFormatter, String.valueOf(rowNum));

                // firstName
                out[1] = safeGetString(row, 1, dataFormatter, "");

                // lastName
                out[2] = safeGetString(row, 2, dataFormatter, "");

                // DOB
                out[3] = extractDateFast(row.getCell(3), dataFormatter, isoFormatter, tryFormats);

                // class
                out[4] = safeGetString(row, 4, dataFormatter, "");

                // score (+10)
                out[5] = computeScore(row.getCell(5), dataFormatter);

                csvWriter.writeNext(out);

                // Progress logging & flush every 100k rows
                if (rowNum % 100_000 == 0) {
                    System.out.println("Processed rows: " + rowNum);
                    bw.flush();
                }
            }
        }

        return csvFilePath.toString();
    }

    // Helpers
    private static String safeGetString(Row row, int idx, DataFormatter formatter, String defaultVal) {
        Cell c = row.getCell(idx);
        if (c == null) return defaultVal;
        String s = formatter.formatCellValue(c);
        return (s == null || s.trim().isEmpty()) ? defaultVal : s;
    }

    private static String extractDateFast(Cell dateCell, DataFormatter formatter,
                                          DateTimeFormatter isoFormatter, DateTimeFormatter[] tryFormats) {
        if (dateCell == null) return LocalDate.now().toString();
        try {
            if (DateUtil.isCellDateFormatted(dateCell)) {
                return dateCell.getLocalDateTimeCellValue().toLocalDate().toString();
            }
            String dateStr = formatter.formatCellValue(dateCell).trim();
            if (dateStr.isEmpty()) return LocalDate.now().toString();

            if (dateStr.length() >= 10 && dateStr.charAt(4) == '-' && dateStr.charAt(7) == '-') {
                return dateStr.substring(0, 10);
            }
            for (DateTimeFormatter f : tryFormats) {
                try {
                    LocalDate d = LocalDate.parse(dateStr, f);
                    return d.toString();
                } catch (Exception ignored) {}
            }
        } catch (Exception e) {
            // ignore, fallback below
        }
        return LocalDate.now().toString();
    }

    private static String computeScore(Cell scoreCell, DataFormatter formatter) {
        if (scoreCell == null) return "70"; // default (60+10)
        try {
            if (scoreCell.getCellType() == CellType.NUMERIC) {
                int val = (int) Math.round(scoreCell.getNumericCellValue());
                return String.valueOf(val + 10);
            } else {
                String s = formatter.formatCellValue(scoreCell).trim();
                if (s.isEmpty()) return "70";
                int val = Integer.parseInt(s.replaceAll("[^0-9-]", ""));
                return String.valueOf(val + 10);
            }
        } catch (Exception e) {
            return "70";
        }
    }

    private static boolean isDateCell(Cell cell) {
        if (cell == null || cell.getCellType() != CellType.NUMERIC) {
            return false;
        }
        // Simple heuristic: check if the cell format contains date-like patterns
        String formatString = cell.getCellStyle().getDataFormatString();
        return formatString != null && (
            formatString.contains("d") || formatString.contains("m") || formatString.contains("y") ||
            formatString.contains("h") || formatString.contains("s") || formatString.toLowerCase().contains("date")
        );
    }

    private String getStoragePath() {
        String os = System.getProperty("os.name").toLowerCase();
        return os.contains("win") ? windowsStoragePath : linuxStoragePath;
    }
}
