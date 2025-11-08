# Windows Setup Guide - Notes Application with H2 Database

This guide provides step-by-step instructions to run the Notes Application on a Windows machine using H2 database.

## Prerequisites

Before you begin, ensure you have the following installed on your Windows machine:

1. **Java Development Kit (JDK) 17 or higher**
   - Download from: https://adoptium.net/ or https://www.oracle.com/java/technologies/downloads/
   - Verify installation: Open Command Prompt and run `java -version`

2. **Apache Maven 3.6 or higher**
   - Download from: https://maven.apache.org/download.cgi
   - Extract to a folder (e.g., `C:\Program Files\Apache\maven`)
   - Add Maven to PATH environment variable
   - Verify installation: Open Command Prompt and run `mvn -version`

3. **Git (Optional, for cloning the repository)**
   - Download from: https://git-scm.com/download/win
   - Verify installation: Open Command Prompt and run `git --version`

## Installation Steps

### Step 1: Get the Source Code

If you have Git installed:
```cmd
git clone https://github.com/dsaswal/0006_2023_notes-psql-v0.4.git
cd 0006_2023_notes-psql-v0.4
git checkout claude/switch-to-h2-database-011CUvGmCZecjFuJFtnN5v9Q
```

Or download the ZIP file from GitHub and extract it to your desired location.

### Step 2: Build the Application

Open Command Prompt or PowerShell, navigate to the project directory, and run:

```cmd
mvn clean package -DskipTests
```

This command will:
- Download all required dependencies
- Compile the source code
- Package the application into a JAR file
- Skip running tests for faster build

The build process may take a few minutes the first time as Maven downloads dependencies.

### Step 3: Run the Application

After successful build, run the application using:

```cmd
java -jar target\notes-psql-v0.4-0.0.1-SNAPSHOT.jar
```

Or use Maven to run directly:

```cmd
mvn spring-boot:run
```

### Step 4: Access the Application

Once the application starts successfully, you should see log messages indicating the server is running.

**Application URL:** http://localhost:8080

**H2 Database Console:** http://localhost:8080/h2-console

## Important Credentials and Configuration

### Database Credentials

The H2 database is configured with the following credentials:

- **JDBC URL:** `jdbc:h2:file:./data/notesdb`
- **Username:** `admin`
- **Password:** `admin123`
- **Driver Class:** `org.h2.Driver`

### H2 Console Access

To access the H2 database console (for database administration):

1. Open your browser and navigate to: http://localhost:8080/h2-console
2. Enter the following details:
   - **JDBC URL:** `jdbc:h2:file:./data/notesdb`
   - **User Name:** `admin`
   - **Password:** `admin123`
3. Click "Connect"

**Note:** The H2 console is only accessible from localhost for security reasons.

### Application User Credentials

The application may have pre-configured users (check your security configuration files). Common default credentials might be:

- **Admin User:**
  - Username: `admin`
  - Password: Check `src/main/resources/config/` files or initialization code

- **Regular User:**
  - Username: `user`
  - Password: Check `src/main/resources/config/` files or initialization code

**Important:** Change these default passwords in a production environment!

## Database Storage Location

The H2 database files are stored in:
```
./data/notesdb.mv.db
```

This creates a `data` folder in your project root directory. The database persists data between application restarts.

To reset the database, simply:
1. Stop the application
2. Delete the `data` folder
3. Restart the application (it will create a fresh database)

## Troubleshooting

### Issue: "JAVA_HOME is not set"

**Solution:**
1. Right-click "This PC" or "My Computer" and select "Properties"
2. Click "Advanced system settings"
3. Click "Environment Variables"
4. Under "System Variables", click "New"
5. Variable name: `JAVA_HOME`
6. Variable value: Path to your JDK installation (e.g., `C:\Program Files\Java\jdk-17`)
7. Click "OK" and restart Command Prompt

### Issue: "mvn is not recognized"

**Solution:**
1. Follow the same steps as above to open Environment Variables
2. Find the "Path" variable under "System Variables" and click "Edit"
3. Click "New" and add: `C:\Program Files\Apache\maven\bin` (adjust to your Maven installation path)
4. Click "OK" and restart Command Prompt

### Issue: Port 8080 is already in use

**Solution:**
Either:
- Stop the application using port 8080, OR
- Change the application port by adding this to `src/main/resources/application.properties`:
  ```properties
  server.port=8081
  ```
  Then access the app at http://localhost:8081

### Issue: Database locked error

**Solution:**
- Make sure only one instance of the application is running
- If the error persists, delete the `data` folder and restart

## Running as Windows Service (Optional)

To run the application as a Windows service, you can use tools like:
- NSSM (Non-Sucking Service Manager): https://nssm.cc/
- Windows Task Scheduler (for automatic startup)

## Stopping the Application

Press `Ctrl + C` in the Command Prompt window where the application is running.

## Additional Configuration

All application configurations can be found in:
```
src/main/resources/application.properties
```

You can modify settings such as:
- Server port
- Database location
- Logging levels
- Security settings
- Cache configuration

## Security Notes

1. The default H2 console is disabled in production by setting:
   ```properties
   spring.h2.console.enabled=false
   ```

2. Change default database credentials before deploying to production

3. The H2 database used is file-based and suitable for development/testing. For production, consider using a more robust database system.

## Support

For issues or questions:
- Check the application logs in the console output
- Review configuration in `application.properties`
- Consult Spring Boot documentation: https://spring.io/projects/spring-boot
- Check H2 Database documentation: https://www.h2database.com/

## Project Structure

```
notes-psql-v0.4/
├── src/
│   ├── main/
│   │   ├── java/              # Java source code
│   │   └── resources/
│   │       ├── application.properties  # Main configuration
│   │       ├── config/        # Security configuration files
│   │       └── templates/     # Thymeleaf templates
│   └── test/                  # Test files
├── target/                    # Compiled files (generated after build)
├── data/                      # H2 database files (generated at runtime)
├── pom.xml                    # Maven configuration
└── WINDOWS_SETUP.md          # This file
```

## Next Steps

After successfully running the application:
1. Explore the application features at http://localhost:8080
2. Access the H2 console to view database structure
3. Review the code to understand the application architecture
4. Modify configurations as needed for your use case

---

**Note:** This application has been migrated from PostgreSQL to H2 database for easier local development and testing on Windows machines without requiring a separate database server installation.
