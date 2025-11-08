# Windows Testing Guide - RBAC Notes Application

## 🚀 Quick Start (No Database Setup Required!)

The application now uses **H2 in-memory database** - perfect for testing on Windows without any database installation.

---

## ✅ Prerequisites

**1. Java 17 or higher**
```cmd
java -version
```

**2. Maven 3.x**
```cmd
mvn -version
```

If you don't have Maven, download from: https://maven.apache.org/download.cgi

---

## 📦 Build the Application

**1. Open Command Prompt or PowerShell**

**2. Navigate to project directory:**
```cmd
cd path\to\0006_2023_notes-psql-v0.4
```

**3. Clean and build:**
```cmd
mvn clean package -DskipTests
```

**Expected output:**
```
[INFO] BUILD SUCCESS
[INFO] ------------------------------------------------------------------------
[INFO] Total time: XX.XXX s
```

---

## ▶️ Run the Application

**Start the application:**
```cmd
mvn spring-boot:run
```

**Wait for this message:**
```
Started NotesAppApplication in X.XXX seconds
```

**The application will:**
- ✅ Create an in-memory H2 database
- ✅ Auto-create all tables (users, roles, permissions, etc.)
- ✅ Load 14 permissions from config/permissions.yml
- ✅ Load 8 roles from config/roles.yml
- ✅ Create 4 default users with different access levels

---

## 🧪 Test the RBAC System

### **1. Access the Application**

Open browser: **http://localhost:8080**

You'll be redirected to the login page.

---

### **2. Test Different User Roles**

#### **🔴 Test as ADMIN (Full Access)**

```
Username: admin
Password: admin123
```

**After login:**
- View admin dashboard: http://localhost:8080/admin/roles
- View all roles, permissions, and user assignments
- Create/modify/delete notes
- Manage roles and permissions

---

#### **🟢 Test as EDITOR (Create/Modify/Delete Notes)**

**Logout first:** Click "Logout" or visit http://localhost:8080/logout

**Login:**
```
Username: dsa
Password: Tiger
```

**Can access:**
- ✅ GET /notes - View all notes
- ✅ POST /notes - Create notes
- ✅ PUT /notes/{id} - Update notes
- ✅ DELETE /notes/{id} - Delete notes

**Cannot access:**
- ❌ /admin/roles - Admin UI (403 Forbidden)

---

#### **🔵 Test as VIEWER (Read-Only)**

**Logout and login:**
```
Username: viewer
Password: viewer123
```

**Can access:**
- ✅ GET /notes - View all notes

**Cannot access:**
- ❌ POST /notes - Create (403 Forbidden)
- ❌ PUT /notes - Update (403 Forbidden)
- ❌ DELETE /notes - Delete (403 Forbidden)
- ❌ /admin/roles - Admin UI (403 Forbidden)

---

#### **🟡 Test as AUDITOR (Audit Logs)**

**Logout and login:**
```
Username: auditor
Password: auditor123
```

**Can access:**
- ✅ View notes
- ✅ View audit logs (when viewing audit UI)

---

### **3. View Database with H2 Console**

**Access H2 Console:** http://localhost:8080/h2-console

**Connection settings:**
```
JDBC URL: jdbc:h2:mem:notesdb
Username: sa
Password: (leave empty)
```

**Click "Connect"**

**Explore tables:**
```sql
-- View all users
SELECT * FROM users;

-- View all roles
SELECT * FROM roles;

-- View all permissions
SELECT * FROM permissions;

-- View role-permission mappings
SELECT r.name as role, p.name as permission
FROM roles r
JOIN role_permissions rp ON r.id = rp.role_id
JOIN permissions p ON rp.permission_id = p.id
ORDER BY r.name;

-- View user-role assignments
SELECT u.username, r.name as role
FROM users u
JOIN user_roles ur ON u.id = ur.user_id
JOIN roles r ON ur.role_id = r.id
ORDER BY u.username;

-- View permission audit logs
SELECT * FROM permission_audit
ORDER BY timestamp DESC;
```

---

### **4. Test with PowerShell (REST API)**

**Create test-rbac.ps1:**

```powershell
# Test script for RBAC system
Write-Host "Testing RBAC System..." -ForegroundColor Green
Write-Host ""

# Test 1: Login as admin and access admin UI
Write-Host "1. Testing ADMIN access to /admin/roles..." -ForegroundColor Yellow
$response = Invoke-WebRequest -Uri "http://localhost:8080/admin/roles" `
    -Credential (New-Object System.Management.Automation.PSCredential("admin", (ConvertTo-SecureString "admin123" -AsPlainText -Force))) `
    -SessionVariable session -ErrorAction SilentlyContinue

if ($response.StatusCode -eq 200) {
    Write-Host "   ✓ Admin can access admin UI" -ForegroundColor Green
} else {
    Write-Host "   ✗ Admin access failed" -ForegroundColor Red
}

Write-Host ""

# Test 2: Login as viewer and try to read notes
Write-Host "2. Testing VIEWER read access..." -ForegroundColor Yellow
$response = Invoke-WebRequest -Uri "http://localhost:8080/notes" `
    -Credential (New-Object System.Management.Automation.PSCredential("viewer", (ConvertTo-SecureString "viewer123" -AsPlainText -Force))) `
    -ErrorAction SilentlyContinue

if ($response.StatusCode -eq 200) {
    Write-Host "   ✓ Viewer can read notes" -ForegroundColor Green
} else {
    Write-Host "   ✗ Viewer read failed" -ForegroundColor Red
}

Write-Host ""

# Test 3: Try to delete as viewer (should fail)
Write-Host "3. Testing VIEWER delete (should be denied)..." -ForegroundColor Yellow
try {
    $response = Invoke-WebRequest -Uri "http://localhost:8080/notes/1" `
        -Method Delete `
        -Credential (New-Object System.Management.Automation.PSCredential("viewer", (ConvertTo-SecureString "viewer123" -AsPlainText -Force))) `
        -ErrorAction Stop
    Write-Host "   ✗ Viewer should NOT be able to delete" -ForegroundColor Red
} catch {
    if ($_.Exception.Response.StatusCode -eq 403) {
        Write-Host "   ✓ Viewer delete correctly denied (403)" -ForegroundColor Green
    } else {
        Write-Host "   ? Unexpected error: $($_.Exception.Message)" -ForegroundColor Yellow
    }
}

Write-Host ""
Write-Host "Testing complete!" -ForegroundColor Green
```

**Run:**
```powershell
.\test-rbac.ps1
```

---

### **5. Test with curl (Command Prompt)**

**Create notes as DSA user:**
```cmd
curl -X POST http://localhost:8080/notes ^
  -H "Content-Type: application/json" ^
  -d "{\"title\":\"Test Note\",\"note\":\"Created via API\"}" ^
  -u dsa:Tiger
```

**Get all notes:**
```cmd
curl http://localhost:8080/notes -u dsa:Tiger
```

**Try to delete as viewer (should fail with 403):**
```cmd
curl -X DELETE http://localhost:8080/notes/1 -u viewer:viewer123 -v
```

---

## 🎯 Verify RBAC Features

### **Test 1: Role Composition**

1. Login as **admin**
2. Go to http://localhost:8080/admin/roles
3. Verify:
   - **EDITOR** inherits from **USER**
   - **MANAGER** inherits from **EDITOR** and has additional USER permissions
   - Each role shows "Direct Permissions" and "Inherited Roles"

---

### **Test 2: Permission Enforcement**

1. Login as **viewer**
2. Open Developer Tools (F12) → Network tab
3. Try to create a note via REST API (should fail)
4. Check H2 console → `permission_audit` table
5. Verify denied attempt is logged

---

### **Test 3: Dynamic Configuration**

**Add a new permission without code changes:**

1. Stop the application (Ctrl+C)
2. Edit: `src\main\resources\config\permissions.yml`

```yaml
- name: NOTES:EXPORT
  resource: NOTES
  action: EXPORT
  description: "Export notes to PDF"
```

3. Restart: `mvn spring-boot:run`
4. Login as **admin**
5. Go to http://localhost:8080/admin/roles
6. Verify **NOTES:EXPORT** appears in permissions list

**Assign to a role:**
- In admin UI, edit **EDITOR** role
- Check the **NOTES:EXPORT** permission
- Save
- Now EDITOR users have export permission

---

## 🔄 Switch Between Databases

### **Currently Using: H2 In-Memory**

**Pros:**
- ✅ No installation required
- ✅ Perfect for testing
- ✅ Fast startup
- ✅ H2 Console for debugging

**Cons:**
- ❌ Data lost on restart
- ❌ Not for production

---

### **To Switch to PostgreSQL:**

**1. Edit `application.properties`:**

Comment out H2 config and uncomment PostgreSQL config:

```properties
# ============================================
# Database Configuration - H2 In-Memory
# ============================================
#spring.datasource.url=jdbc:h2:mem:notesdb
#spring.datasource.driver-class-name=org.h2.Driver
#spring.datasource.username=sa
#spring.datasource.password=
#spring.h2.console.enabled=true
#spring.h2.console.path=/h2-console
#spring.jpa.database=H2
#spring.jpa.database-platform=org.hibernate.dialect.H2Dialect
#spring.jpa.hibernate.ddl-auto=create-drop

# ============================================
# PostgreSQL Configuration (Active)
# ============================================
spring.datasource.url=jdbc:postgresql://database-1.cbqdfona2szl.eu-north-1.rds.amazonaws.com:5432/notesdb04
spring.datasource.username=postgres
spring.datasource.password=pgpass123
spring.datasource.driver-class-name=org.postgresql.Driver
spring.jpa.database=POSTGRESQL
spring.jpa.database-platform=org.hibernate.dialect.PostgreSQLDialect
spring.jpa.hibernate.ddl-auto=update
```

**2. Rebuild:**
```cmd
mvn clean package -DskipTests
```

**3. Run:**
```cmd
mvn spring-boot:run
```

---

## 🐛 Troubleshooting

### **Issue: Port 8080 already in use**

**Solution:**
```cmd
netstat -ano | findstr :8080
taskkill /PID <PID> /F
```

Or change port in `application.properties`:
```properties
server.port=8081
```

---

### **Issue: Build fails - "Cannot resolve dependency"**

**Solution:**
```cmd
mvn clean install -U
```

Force update dependencies.

---

### **Issue: 403 Forbidden on all endpoints**

**Check:**
1. Logged in with correct user
2. User has required role
3. Role has required permission

**Verify in H2 Console:**
```sql
SELECT u.username, r.name as role, p.name as permission
FROM users u
JOIN user_roles ur ON u.id = ur.user_id
JOIN roles r ON ur.role_id = r.id
JOIN role_permissions rp ON r.id = rp.role_id
JOIN permissions p ON rp.permission_id = p.id
WHERE u.username = 'dsa';
```

---

### **Issue: Login page not loading**

**Check:**
1. Application started successfully
2. Look for: `Started NotesAppApplication`
3. No errors in console

---

## 📊 View Application Logs

**PowerShell:**
```powershell
mvn spring-boot:run | Tee-Object -FilePath application.log
```

**Command Prompt:**
```cmd
mvn spring-boot:run > application.log 2>&1
```

**Look for:**
- `Loading permission and role configurations...`
- `Loaded 14 permissions from configuration`
- `Loaded 8 roles from configuration`
- `Created default admin user`
- `Permission GRANTED/DENIED for user...`

---

## ✅ Success Checklist

Your RBAC system is working if:

- ✅ Application starts without errors
- ✅ Can login with all 4 users (admin, dsa, viewer, auditor)
- ✅ Admin can access `/admin/roles`
- ✅ Viewer cannot delete notes (gets 403)
- ✅ DSA can create/modify/delete notes
- ✅ H2 Console shows all tables populated
- ✅ `permission_audit` table has records
- ✅ New permissions can be added via YAML

---

## 📝 Default User Credentials

| Username | Password | Roles | Access |
|----------|----------|-------|--------|
| admin | admin123 | ADMIN | Full system access |
| dsa | Tiger | USER, EDITOR | CRUD operations on notes |
| viewer | viewer123 | VIEWER | Read-only |
| auditor | auditor123 | AUDITOR, SECURITY_OFFICER | Audit logs |

---

## 🎓 Next Steps

1. **Explore Admin UI:** Create custom roles, assign permissions
2. **Test API:** Use Postman or curl to test REST endpoints
3. **View Audit Logs:** Check H2 console for permission checks
4. **Add Custom Permissions:** Edit YAML files, restart, verify
5. **Read Documentation:** See `RBAC_IMPLEMENTATION.md` for architecture details

---

**Need Help?** Check `RBAC_IMPLEMENTATION.md` for complete architecture documentation.
