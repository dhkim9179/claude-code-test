# Logging Configuration Guide

This document explains the logging configuration for the demo application across different environments.

## Overview

The application uses Logback for logging with environment-specific configurations for:
- **local**: Local development environment
- **dev**: Development server environment
- **tb**: Test Bed environment
- **prd1**: Production 1 environment
- **prd2**: Production 2 environment

## Configuration Files

- `logback-spring.xml`: Main Logback configuration with Spring profile support
- `application-{profile}.properties`: Profile-specific application properties

## Environment-Specific Logging Levels

### Local Environment
- **Purpose**: Local development
- **Log Level**: DEBUG for application code, INFO for frameworks
- **Output**: Console only
- **Details**: Most verbose logging for debugging
- **Package Levels**:
  - `com.example.demo`: DEBUG
  - `org.springframework.batch`: DEBUG
  - `org.mybatis`: DEBUG

### Dev Environment
- **Purpose**: Development server
- **Log Level**: DEBUG for application code
- **Output**: Console + File + Error File
- **Details**: Verbose logging with file persistence
- **Package Levels**:
  - `com.example.demo`: DEBUG
  - `org.springframework.batch`: DEBUG
  - `org.mybatis`: DEBUG
  - `org.apache.ibatis`: DEBUG

### TB (Test Bed) Environment
- **Purpose**: Testing environment
- **Log Level**: INFO
- **Output**: Console + File + Error File
- **Details**: Standard logging for testing
- **Package Levels**:
  - `com.example.demo`: INFO
  - `org.springframework`: INFO
  - `org.springframework.batch`: INFO

### PRD1 and PRD2 Environments
- **Purpose**: Production environments
- **Log Level**: WARN (batch operations at INFO)
- **Output**: Console + File + Error File
- **Details**: Minimal logging for production
- **Package Levels**:
  - `com.example.demo`: WARN
  - `org.springframework`: WARN
  - `org.springframework.batch`: INFO (to track batch jobs)
  - `org.mybatis`: WARN

## Log File Configuration

### File Locations
- **Local/Dev**: `./logs/` (relative to application directory)
- **TB/PRD1/PRD2**: `/var/log/demo/` (system log directory)

### File Naming Pattern
- Main log: `demo-{profile}.log`
- Error log: `demo-{profile}-error.log`

### Rolling Policy
- **Daily Rotation**: Log files roll over daily
- **Size Limit**: Each file up to 100MB
- **Retention**:
  - Main logs: 30 days
  - Error logs: 90 days
- **Total Size Cap**:
  - Main logs: 3GB
  - Error logs: 5GB

### Log File Pattern
```
yyyy-MM-dd HH:mm:ss.SSS [thread] LEVEL logger - message
```

Example:
```
2025-10-28 15:30:45.123 [main] INFO  com.example.demo.Application - Application started
```

## How to Use

### Activate a Profile

#### Option 1: Using application.properties
Set the active profile in `application.properties`:
```properties
spring.profiles.active=local
```

#### Option 2: Using Command Line
```bash
# Run with local profile
./gradlew bootRun --args='--spring.profiles.active=local'

# Run with dev profile
./gradlew bootRun --args='--spring.profiles.active=dev'

# Run with tb profile
./gradlew bootRun --args='--spring.profiles.active=tb'

# Run with prd1 profile
./gradlew bootRun --args='--spring.profiles.active=prd1'

# Run with prd2 profile
./gradlew bootRun --args='--spring.profiles.active=prd2'
```

#### Option 3: Using Environment Variable
```bash
export SPRING_PROFILES_ACTIVE=local
./gradlew bootRun
```

#### Option 4: Using Java System Property
```bash
java -jar -Dspring.profiles.active=dev demo.jar
```

### Profile-Specific Properties Files

You can also use the profile-specific properties files:
```bash
# Use local profile
./gradlew bootRun --args='--spring.config.additional-location=classpath:application-local.properties'
```

## Customizing Log Paths

You can override the default log paths using environment variables or system properties:

```bash
# Custom log path
java -jar -DLOG_PATH=/custom/log/path demo.jar

# Custom log file name
java -jar -DLOG_FILE=myapp demo.jar
```

Or in application.properties:
```properties
logging.file.path=/custom/log/path
logging.file.name=myapp
```

## Viewing Logs

### Tail logs in real-time
```bash
# Main log
tail -f logs/demo-local.log

# Error log
tail -f logs/demo-local-error.log
```

### Search logs
```bash
# Find ERROR entries
grep ERROR logs/demo-local.log

# Find specific pattern
grep "BatchJob" logs/demo-local.log
```

## Best Practices

1. **Local Development**: Use `local` profile for day-to-day development
2. **Development Server**: Use `dev` profile for shared development environment
3. **Testing**: Use `tb` profile for integration and acceptance testing
4. **Production**: Use `prd1` or `prd2` profile for production deployments
5. **Monitor Disk Space**: Regularly check log directory size in production
6. **Archive Old Logs**: Consider archiving logs older than retention period
7. **Error Monitoring**: Set up alerts for error log files in production
8. **Sensitive Data**: Never log sensitive information (passwords, tokens, personal data)

## Troubleshooting

### Logs not appearing
1. Check if the profile is correctly activated: Look for "The following profiles are active" in console output
2. Verify log file path exists and has write permissions
3. Check if logback-spring.xml is in the classpath

### Log files growing too large
1. Adjust `maxFileSize` in logback-spring.xml
2. Reduce `maxHistory` retention period
3. Lower the `totalSizeCap` limit
4. Review and reduce log levels for noisy packages

### Cannot find log files
1. Check `LOG_PATH` property value
2. Verify directory permissions
3. Look for logs in application working directory (./logs) by default

## Additional Resources

- [Spring Boot Logging Documentation](https://docs.spring.io/spring-boot/docs/current/reference/html/features.html#features.logging)
- [Logback Documentation](http://logback.qos.ch/documentation.html)
- [Spring Profiles](https://docs.spring.io/spring-boot/docs/current/reference/html/features.html#features.profiles)
