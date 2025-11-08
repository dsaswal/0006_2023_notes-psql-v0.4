# Quick Startup Guide - H2 Database Version

## TL;DR - Quick Commands

```cmd
# Build
mvn clean package -DskipTests

# Run
java -jar target\notes-psql-v0.4-0.0.1-SNAPSHOT.jar

# Or run with Maven
mvn spring-boot:run
```

**Access:** http://localhost:8080

## What Successful Startup Looks Like

When the application starts successfully, you'll see log output similar to this:

```
  .   ____          _            __ _ _
 /\\ / ___'_ __ _ _(_)_ __  __ _ \ \ \ \
( ( )\___ | '_ | '_| | '_ \/ _` | \ \ \ \
 \\/  ___)| |_)| | | | | || (_| |  ) ) ) )
  '  |____| .__|_| |_|_| |_\__, | / / / /
 =========|_|==============|___/=/_/_/_/
 :: Spring Boot ::                (v3.1.0)

INFO  Starting NotesAppApplication...
INFO  Loading permissions from YAML configuration...
INFO  Loaded X permissions from configuration
INFO  Loading roles from YAML configuration...
INFO  Loaded X roles from configuration
INFO  Loading bootstrap data...
INFO  Created default admin user (username: admin, password: admin123)
INFO  Created default user (username: dsa, password: Tiger)
INFO  Created viewer user (username: viewer, password: viewer123)
INFO  Created auditor user (username: auditor, password: auditor123)
INFO  Total users in system: 4
INFO  Bootstrap data loaded successfully
INFO  Started NotesAppApplication in X.XXX seconds
```

**Key indicators of success:**
- "Started NotesAppApplication" message
- No ERROR or FATAL messages
- Shows port number (default: 8080)
- Users created successfully

## Understanding the Exit Code 1 Error

**"Process terminated with exit code: 1"** is a generic error. The real issue is shown BEFORE this message in the console output.

### How to Diagnose:

Run the application and scroll up to find the FIRST error message:

```cmd
mvn spring-boot:run
```

Then look for lines starting with:
- `ERROR` - Application error
- `FATAL` - Critical error
- `Exception` - Exception stack trace
- `Caused by:` - Root cause

### Common Error Patterns and Solutions:

#### 1. Port Already in Use
```
ERROR: Web server failed to start. Port 8080 was already in use.
```
**Solution:**
- Stop other applications on port 8080, OR
- Change port in `application.properties`:
  ```properties
  server.port=8081
  ```

#### 2. Database Lock Error
```
ERROR: Database may be already in use: "Locked by another process"
```
**Solution:**
- Stop all instances of the application
- Delete the `data` folder
- Restart

#### 3. Missing Configuration Files
```
ERROR: Could not find resource: classpath:config/permissions.yml
ERROR: Could not find resource: classpath:config/roles.yml
```
**Solution:**
- Verify these files exist:
  - `src/main/resources/config/permissions.yml`
  - `src/main/resources/config/roles.yml`
- If missing, restore from git

#### 4. H2 Driver Not Found
```
ERROR: Cannot load driver class: org.h2.Driver
```
**Solution:**
- Rebuild the application:
  ```cmd
  mvn clean install
  ```

#### 5. OutOfMemoryError
```
ERROR: java.lang.OutOfMemoryError: Java heap space
```
**Solution:**
- Increase JVM memory:
  ```cmd
  java -Xmx512m -jar target\notes-psql-v0.4-0.0.1-SNAPSHOT.jar
  ```

#### 6. Permission Denied / Cannot Create Directory
```
ERROR: Could not create directory: ./data
```
**Solution:**
- Run Command Prompt as Administrator, OR
- Choose a different location for the database by editing `application.properties`:
  ```properties
  spring.datasource.url=jdbc:h2:file:C:/temp/notesdb;DB_CLOSE_ON_EXIT=FALSE;AUTO_SERVER=TRUE
  ```

## Complete Diagnostic Process

If you encounter exit code 1, follow these steps:

### Step 1: Capture Full Error Output
```cmd
mvn spring-boot:run > startup.log 2>&1
```

Then open `startup.log` in a text editor and search for:
- First occurrence of `ERROR`
- First occurrence of `Exception`

### Step 2: Clean Build
```cmd
# Delete previous build
rmdir /s /q target

# Fresh build
mvn clean package -DskipTests
```

### Step 3: Run with Verbose Logging
```cmd
java -jar target\notes-psql-v0.4-0.0.1-SNAPSHOT.jar --debug
```

### Step 4: Verify Environment
```cmd
# Check Java version (requires 17+)
java -version

# Check Maven version
mvn -version

# Verify disk space
dir C:\

# Check write permissions
echo test > test.txt
del test.txt
```

### Step 5: Test with Minimal Configuration

Create a test configuration at `src/main/resources/application-test.properties`:
```properties
server.port=9090
spring.datasource.url=jdbc:h2:mem:testdb
spring.datasource.username=sa
spring.datasource.password=
spring.h2.console.enabled=true
spring.jpa.hibernate.ddl-auto=create
```

Then run with test profile:
```cmd
java -jar target\notes-psql-v0.4-0.0.1-SNAPSHOT.jar --spring.profiles.active=test
```

## Database Credentials Reference

### H2 Database Console
- **URL:** http://localhost:8080/h2-console
- **JDBC URL:** `jdbc:h2:file:./data/notesdb`
- **Username:** `admin`
- **Password:** `admin123`

### Application Users
| Username | Password | Roles |
|----------|----------|-------|
| admin | admin123 | ADMIN |
| dsa | Tiger | USER, EDITOR |
| viewer | viewer123 | VIEWER |
| auditor | auditor123 | AUDITOR, SECURITY_OFFICER |

## Verifying the Application is Running

### 1. Check the Console
Look for "Started NotesAppApplication" message

### 2. Test HTTP Endpoints
Open browser or use curl:
```cmd
curl http://localhost:8080
```

Should return the login page HTML or redirect to login.

### 3. Access H2 Console
Navigate to: http://localhost:8080/h2-console

If this loads, the application is running!

### 4. Check Processes (Windows)
```cmd
netstat -ano | findstr :8080
```

Should show a process listening on port 8080.

## Network Troubleshooting

If Maven can't download dependencies:

```
[ERROR] Failed to transfer artifact... Connection timed out
[ERROR] Unknown host repo.maven.apache.org
```

**Solutions:**
1. Check internet connection
2. Check firewall/antivirus settings
3. Configure Maven proxy (if behind corporate firewall)
4. Use a Maven mirror

Create/edit `C:\Users\YourUsername\.m2\settings.xml`:
```xml
<settings>
  <mirrors>
    <mirror>
      <id>central</id>
      <url>https://repo1.maven.org/maven2</url>
      <mirrorOf>central</mirrorOf>
    </mirror>
  </mirrors>
</settings>
```

## Still Having Issues?

1. **Check the actual error message** - Don't just look at "exit code 1"
2. **Read the full stack trace** - The root cause is usually at the bottom
3. **Enable debug logging** - Add `--debug` flag
4. **Test basic functionality** - Can you compile? Can you run other Java apps?
5. **Fresh clone** - Try cloning the repository again
6. **Check file integrity** - Ensure all files were cloned correctly

## Success Checklist

- [ ] Java 17+ installed and in PATH
- [ ] Maven 3.6+ installed and in PATH
- [ ] Internet connection available (for first build)
- [ ] Sufficient disk space (at least 500MB free)
- [ ] Write permissions in project directory
- [ ] Port 8080 available (or configured to use different port)
- [ ] Config files present: `permissions.yml` and `roles.yml`
- [ ] Build successful: JAR created in `target/` directory
- [ ] Application starts without ERROR messages
- [ ] Can access http://localhost:8080
- [ ] Can login with admin/admin123

---

For detailed setup instructions, see [WINDOWS_SETUP.md](WINDOWS_SETUP.md)
