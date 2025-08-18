# Local PostgreSQL Setup Guide

## Prerequisites
1. PostgreSQL installed on your local machine
2. Java 17+ installed
3. Maven installed

## Step 1: Install PostgreSQL

### Windows
1. Download PostgreSQL from: https://www.postgresql.org/download/windows/
2. Run the installer
3. Remember the password you set for the `postgres` user
4. Default port is 5432

### macOS
```bash
brew install postgresql
brew services start postgresql
```

### Linux (Ubuntu/Debian)
```bash
sudo apt update
sudo apt install postgresql postgresql-contrib
sudo systemctl start postgresql
sudo systemctl enable postgresql
```

## Step 2: Create Database and User

1. Connect to PostgreSQL as the postgres user:
   ```bash
   # Windows (if added to PATH)
   psql -U postgres
   
   # Or use pgAdmin if installed
   ```

2. Create the database:
   ```sql
   CREATE DATABASE student_processor;
   ```

3. Create a user (optional):
   ```sql
   CREATE USER student_user WITH PASSWORD 'your_password';
   GRANT ALL PRIVILEGES ON DATABASE student_processor TO student_user;
   ```

## Step 3: Update Configuration (if needed)

If you want to use different credentials, update `application.yml`:

```yaml
spring:
  datasource:
    url: jdbc:postgresql://localhost:5432/student_processor
    username: your_username
    password: your_password
```

## Step 4: Test the Application

1. Start your Spring Boot application:
   ```bash
   mvn spring-boot:run
   ```

2. Check the logs for successful database connection

## Step 5: Verify Database Tables

The application will automatically create tables using JPA's `ddl-auto: update` setting.

## Troubleshooting

### Connection Refused
- Ensure PostgreSQL service is running
- Check if port 5432 is available
- Verify firewall settings

### Authentication Failed
- Check username/password in `application.yml`
- Verify user has access to the database

### Port Already in Use
- Change PostgreSQL port in `postgresql.conf`
- Update `application.yml` with new port

## Default Configuration

- **Host**: localhost
- **Port**: 5432
- **Database**: student_processor
- **Username**: postgres
- **Password**: postgres

## Benefits of Local PostgreSQL

✅ **No network issues**
✅ **Faster development**
✅ **Full control over database**
✅ **No internet dependency**
✅ **Easier debugging**
✅ **Free to use**
