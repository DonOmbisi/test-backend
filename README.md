# Student Data Processor - Backend

A Spring Boot application for processing student data with Excel generation, CSV conversion, database operations, and reporting capabilities.

## 🚀 Features

- **Data Generation**: Generate Excel files with random student data (up to 1M records)
- **Data Processing**: Convert Excel files to CSV with score updates
- **Database Operations**: Upload CSV data to Supabase PostgreSQL
- **Reporting**: View, filter, and export student data in multiple formats
- **Performance Optimized**: Handles large datasets efficiently with batch processing

## 🛠️ Tech Stack

- **Java**: 17+
- **Spring Boot**: 3.4.5
- **Database**: Supabase (PostgreSQL)
- **File Processing**: Apache POI (Excel), OpenCSV, iText7 (PDF)
- **Build Tool**: Maven

## 📋 Prerequisites

- Java 17 or higher
- Maven 3.6+
- Supabase account and database

## ⚙️ Configuration

### 1. Environment Setup

**⚠️ IMPORTANT: Follow the security setup guide before running the application!**

See [SETUP_ENVIRONMENT.md](SETUP_ENVIRONMENT.md) for detailed instructions on configuring environment variables securely.

Quick setup:
1. Copy the example environment file: `cp env.example .env`
2. Edit `.env` with your actual database credentials
3. Never commit the `.env` file to version control

### 2. Database Setup

The application will automatically create the `students` table with the following schema:

```sql
CREATE TABLE students (
    studentId BIGINT PRIMARY KEY AUTO_INCREMENT,
    firstName VARCHAR(50) NOT NULL,
    lastName VARCHAR(50) NOT NULL,
    DOB DATE NOT NULL,
    class VARCHAR(20) NOT NULL,
    score INTEGER NOT NULL
);
```

### 3. File Storage Paths

Files are stored in the following locations:
- **Windows**: `C:\var\log\applications\API\dataprocessing\`
- **Linux**: `/var/log/applications/API/dataprocessing/`

## 🚀 Running the Application

### 1. Clone and Navigate

```bash
cd backend
```

### 2. Install Dependencies

```bash
mvn clean install
```

### 3. Run the Application

```bash
mvn spring-boot:run
```

The application will start on `http://localhost:8080/api`

## 📚 API Endpoints

### Data Generation

#### Generate Excel File
```
POST /api/data-generation/generate-excel
Content-Type: application/json

{
    "numberOfRecords": 1000
}
```

### Data Processing

#### Convert Excel to CSV
```
POST /api/data-processing/excel-to-csv
Content-Type: multipart/form-data

file: [Excel file]
```

### Database Operations

#### Upload CSV to Database
```
POST /api/database/upload-csv
Content-Type: multipart/form-data

file: [CSV file]
```

#### Get Total Student Count
```
GET /api/database/total-count
```

### Reporting

#### Get Students (with pagination and filters)
```
GET /api/reports/students?page=0&size=20&studentId=123&className=Class1
```

#### Export to Excel
```
POST /api/reports/export/excel
Content-Type: application/json

{
    "page": 0,
    "size": 20,
    "studentId": null,
    "className": "Class1"
}
```

#### Export to CSV
```
POST /api/reports/export/csv
Content-Type: application/json

{
    "page": 0,
    "size": 20,
    "studentId": null,
    "className": "Class1"
}
```

#### Export to PDF
```
POST /api/reports/export/pdf
Content-Type: application/json

{
    "page": 0,
    "size": 20,
    "studentId": null,
    "className": "Class1"
}
```

## 🔧 Development

### Project Structure

```
src/main/java/com/example/
├── controller/          # REST controllers
├── service/            # Business logic services
├── repository/         # Data access layer
├── entity/            # JPA entities
└── dto/               # Data transfer objects
```

### Building

```bash
mvn clean package
```

### Testing

```bash
mvn test
```

## 📊 Performance Considerations

- **Batch Processing**: Database operations use batch inserts (1000 records per batch)
- **Memory Management**: Large file processing uses streaming to avoid memory issues
- **Async Processing**: Long-running operations can be made asynchronous
- **Database Indexing**: Consider adding indexes on frequently queried fields

## 🚨 Error Handling

The application includes comprehensive error handling for:
- File validation (type, size, format)
- Database connection issues
- File processing errors
- Invalid input parameters

## 🔒 Security Notes

- CORS is enabled for development (configure appropriately for production)
- Database credentials should be stored securely using environment variables
- File uploads are validated for type and content

## 📝 License

This project is licensed under the MIT License.
