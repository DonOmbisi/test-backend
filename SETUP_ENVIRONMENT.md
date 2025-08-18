# Environment Setup Guide

## 🔐 Security Configuration

This application uses environment variables to keep sensitive information secure. Follow these steps to set up your environment:

## 📋 Required Environment Variables

### 1. Database Configuration

Create a `.env` file in the backend root directory with the following variables:

```bash
# Database Configuration
DATABASE_URL=jdbc:postgresql://localhost:5432/student_processor
DATABASE_USERNAME=postgres
DATABASE_PASSWORD=your_actual_database_password
```

### 2. Server Configuration (Optional)

```bash
# Server Configuration
SERVER_PORT=8081
```

### 3. File Storage Paths (Optional)

```bash
# File Storage Paths
STORAGE_PATH_WINDOWS=C:/var/log/applications/API/dataprocessing
STORAGE_PATH_LINUX=/var/log/applications/API/dataprocessing
```

## 🚀 Setup Instructions

### Option 1: Using .env file (Recommended)

1. Copy the example file:
   ```bash
   cp env.example .env
   ```

2. Edit the `.env` file with your actual credentials:
   ```bash
   # Replace with your actual database password
   DATABASE_PASSWORD=your_actual_password_here
   ```

3. The application will automatically load these environment variables.

### Option 2: System Environment Variables

Set the environment variables in your system:

**Windows:**
```cmd
set DATABASE_URL=jdbc:postgresql://localhost:5432/student_processor
set DATABASE_USERNAME=postgres
set DATABASE_PASSWORD=your_password
```

**Linux/Mac:**
```bash
export DATABASE_URL=jdbc:postgresql://localhost:5432/student_processor
export DATABASE_USERNAME=postgres
export DATABASE_PASSWORD=your_password
```

### Option 3: IDE Configuration

If using an IDE like IntelliJ IDEA or Eclipse:

1. Go to Run Configuration
2. Add environment variables:
   - `DATABASE_URL`
   - `DATABASE_USERNAME`
   - `DATABASE_PASSWORD`

## 🔒 Security Best Practices

1. **Never commit `.env` files** to version control
2. **Use strong passwords** for database access
3. **Limit database user permissions** to only what's necessary
4. **Use different credentials** for development, testing, and production
5. **Regularly rotate passwords** in production environments

## 🐳 Docker Support

If using Docker, you can pass environment variables:

```bash
docker run -e DATABASE_URL=your_url -e DATABASE_PASSWORD=your_password your-app
```

Or use a docker-compose file:

```yaml
version: '3.8'
services:
  app:
    image: your-app
    environment:
      - DATABASE_URL=jdbc:postgresql://db:5432/student_processor
      - DATABASE_USERNAME=postgres
      - DATABASE_PASSWORD=${DATABASE_PASSWORD}
```

## 🚨 Troubleshooting

### Common Issues:

1. **"Database connection failed"**
   - Check if DATABASE_URL is correct
   - Verify DATABASE_USERNAME and DATABASE_PASSWORD
   - Ensure PostgreSQL is running

2. **"Environment variable not found"**
   - Verify the variable name matches exactly
   - Check if the .env file is in the correct location
   - Restart your IDE/terminal after setting variables

3. **"Permission denied"**
   - Check file permissions for storage directories
   - Ensure the application has write access

## 📝 Example .env File

```bash
# Database Configuration
DATABASE_URL=jdbc:postgresql://localhost:5432/student_processor
DATABASE_USERNAME=postgres
DATABASE_PASSWORD=my_secure_password_123

# Server Configuration
SERVER_PORT=8081

# File Storage Paths
STORAGE_PATH_WINDOWS=C:/var/log/applications/API/dataprocessing
STORAGE_PATH_LINUX=/var/log/applications/API/dataprocessing
```

## 🔍 Verification

To verify your environment is set up correctly:

1. Start the application
2. Check the logs for successful database connection
3. Test the health endpoint: `http://localhost:8081/api/health`

If you see any errors, double-check your environment variables and database configuration.
