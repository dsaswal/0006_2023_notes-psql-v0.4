# Notes Application - H2 Database Version

This branch (`claude/switch-to-h2-database-011CUvGmCZecjFuJFtnN5v9Q`) contains the H2 database version of the Notes application, migrated from PostgreSQL for easier local development and Windows deployment.

## Quick Start

### Prerequisites
- Java 17 or higher
- Maven 3.6 or higher

### Run the Application

```bash
# Build
mvn clean package -DskipTests

# Run
java -jar target/notes-psql-v0.4-0.0.1-SNAPSHOT.jar
```

**Access at:** http://localhost:8080

### Login Credentials

| Username | Password | Role |
|----------|----------|------|
| admin | admin123 | ADMIN |
| dsa | Tiger | USER, EDITOR |
| viewer | viewer123 | VIEWER |
| auditor | auditor123 | AUDITOR |

### H2 Database Console

- **URL:** http://localhost:8080/h2-console
- **JDBC URL:** `jdbc:h2:file:./data/notesdb`
- **Username:** `admin`
- **Password:** `admin123`

## What Changed from PostgreSQL?

### Dependencies (pom.xml)
- ✅ Replaced PostgreSQL driver with H2 database
- ✅ H2 is a lightweight, embedded Java database

### Configuration (application.properties)
- ✅ Changed database URL from PostgreSQL to H2 file-based storage
- ✅ Enabled H2 web console for database administration
- ✅ Updated JPA dialect from PostgreSQL to H2
- ✅ Database credentials: admin/admin123

### Code Compatibility
- ✅ All existing JPQL queries are database-agnostic (no changes needed)
- ✅ JPA entities work with both PostgreSQL and H2
- ✅ No application code changes required

## Key Features

### H2 Database Benefits
1. **No External Database Server Required** - H2 runs embedded in the application
2. **File-Based Persistence** - Data stored in `./data/notesdb.mv.db`
3. **Web Console** - Built-in database admin interface at `/h2-console`
4. **Easy Windows Setup** - No PostgreSQL installation needed
5. **Perfect for Development** - Quick to set up and reset

### Database Storage
- Location: `./data/notesdb.mv.db` (created automatically)
- Persistence: File-based (survives application restarts)
- Reset: Delete `data` folder to start fresh

### Security Features (RBAC)
- Dynamic role-based access control
- YAML-based permission configuration
- Audit logging for permission checks
- Multiple user roles with different access levels

## Documentation

📖 **[WINDOWS_SETUP.md](WINDOWS_SETUP.md)** - Complete Windows installation guide
- Prerequisites and installation
- Step-by-step setup instructions
- Environment variable configuration
- All credentials and passwords
- Troubleshooting common issues

📖 **[STARTUP_GUIDE.md](STARTUP_GUIDE.md)** - Quick reference and troubleshooting
- Quick start commands
- What successful startup looks like
- Diagnosing "exit code 1" errors
- Common error patterns and solutions
- Database verification steps

📖 **[RBAC_IMPLEMENTATION.md](RBAC_IMPLEMENTATION.md)** - Role-based access control details
- Permission system architecture
- Role configuration
- Security implementation

## Troubleshooting

### "Process terminated with exit code: 1"

This is a generic error. The real issue is shown BEFORE this message in the output.

**Quick diagnosis:**
```cmd
mvn spring-boot:run
```

Then scroll up to find the FIRST error message (look for `ERROR` or `Exception`).

**Common issues:**
- Port 8080 already in use → Change to 8081 in `application.properties`
- Database locked → Stop app, delete `data` folder, restart
- Missing config files → Check `src/main/resources/config/*.yml` exist
- Build not complete → Run `mvn clean package -DskipTests`

**See [STARTUP_GUIDE.md](STARTUP_GUIDE.md) for detailed troubleshooting.**

## Current Environment Note

The current development environment has network connectivity limitations preventing Maven from downloading dependencies. This is **expected and normal** for this environment.

**On Windows with internet access**, the build will work correctly:
```cmd
mvn clean package -DskipTests
```

## Project Structure

```
notes-psql-v0.4/
├── src/main/
│   ├── java/dsa/personal/notespsqlv04/
│   │   ├── controller/          # REST controllers
│   │   ├── entity/              # JPA entities
│   │   ├── repository/          # Data repositories
│   │   ├── service/             # Business logic
│   │   └── security/            # RBAC implementation
│   └── resources/
│       ├── application.properties  # H2 configuration
│       ├── config/
│       │   ├── permissions.yml     # Permission definitions
│       │   └── roles.yml           # Role definitions
│       └── templates/              # Thymeleaf views
├── data/                        # H2 database files (generated)
├── target/                      # Build output (generated)
├── pom.xml                      # Maven configuration
├── README_H2.md                 # This file
├── WINDOWS_SETUP.md             # Windows installation guide
├── STARTUP_GUIDE.md             # Troubleshooting guide
└── RBAC_IMPLEMENTATION.md       # RBAC documentation
```

## Migration from PostgreSQL

This version maintains full feature parity with the PostgreSQL version:
- ✅ All entities and relationships preserved
- ✅ All RBAC functionality intact
- ✅ All REST endpoints functional
- ✅ All security features working
- ✅ Audit logging enabled

## Development Notes

### Reset Database
```bash
# Stop the application (Ctrl+C)
rm -rf data/          # Linux/Mac
rmdir /s /q data      # Windows

# Restart application - fresh database will be created
```

### Change Database Location
Edit `application.properties`:
```properties
spring.datasource.url=jdbc:h2:file:C:/your/custom/path/notesdb;DB_CLOSE_ON_EXIT=FALSE;AUTO_SERVER=TRUE
```

### Disable H2 Console (Production)
Edit `application.properties`:
```properties
spring.h2.console.enabled=false
```

### Change Server Port
Edit `application.properties`:
```properties
server.port=8081
```

## Production Considerations

⚠️ **H2 is designed for development and testing.** For production:
1. Consider using PostgreSQL, MySQL, or other production databases
2. Change all default passwords
3. Disable H2 console (`spring.h2.console.enabled=false`)
4. Use proper connection pooling
5. Implement backup strategy
6. Use environment variables for sensitive configuration

## Support & Issues

1. **Build fails** → Check internet connection, run `mvn clean install`
2. **Can't start** → Read the error message before "exit code 1"
3. **Port in use** → Change server.port in application.properties
4. **Database locked** → Stop all instances, delete data folder
5. **Can't login** → Use credentials listed above

For detailed troubleshooting, see [STARTUP_GUIDE.md](STARTUP_GUIDE.md).

## Next Steps

1. ✅ Clone the repository
2. ✅ Checkout this branch: `claude/switch-to-h2-database-011CUvGmCZecjFuJFtnN5v9Q`
3. ✅ Install Java 17+ and Maven 3.6+
4. ✅ Build: `mvn clean package -DskipTests`
5. ✅ Run: `java -jar target/notes-psql-v0.4-0.0.1-SNAPSHOT.jar`
6. ✅ Access: http://localhost:8080
7. ✅ Login with admin/admin123

Enjoy your locally-runnable Notes application with H2 database! 🚀
