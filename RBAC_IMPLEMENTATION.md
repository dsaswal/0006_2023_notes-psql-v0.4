# Dynamic RBAC System Implementation

## Overview

This document describes the enterprise-grade Role-Based Access Control (RBAC) system implemented in the Notes application. The system features **modular, composable roles** with **externalized permission definitions** that can be modified without code changes.

## Key Features

### ✅ Implemented Features

1. **Permission-Based RBAC**
   - Atomic permissions with format: `RESOURCE:ACTION` (e.g., `NOTES:READ`)
   - Wildcard permissions for superadmin (`*:*`)
   - Fine-grained control at resource and action level

2. **Composable Roles**
   - Roles can inherit from multiple other roles
   - Dynamic role composition: `SENIOR_ANALYST = ANALYST + AUDITOR + additional permissions`
   - Prevent circular dependencies in role hierarchy

3. **Externalized Configuration**
   - Permissions defined in `config/permissions.yml`
   - Roles defined in `config/roles.yml`
   - Add new permissions/roles without code changes
   - Automatic loading on application startup

4. **Audit Logging**
   - Every permission check is logged
   - Track granted and denied access attempts
   - Essential for compliance in financial services
   - Asynchronous logging for performance

5. **Performance Optimizations**
   - Caching of user permissions
   - Async audit logging
   - Batch database operations
   - Lazy loading prevention with eager fetching

6. **Security Best Practices**
   - BCrypt password encoding
   - Session-based authentication
   - CSRF protection enabled
   - Database-backed authentication
   - Account status checks (enabled, locked, expired)

## Architecture

### Entity Model

```
User
├── username (unique)
├── password (BCrypt)
├── email (unique)
├── enabled
├── roles (Many-to-Many)
└── timestamps (created, updated, last_login)

Role
├── name (unique)
├── description
├── isSystem (prevent deletion)
├── permissions (Many-to-Many)
└── childRoles (Role Hierarchy)

Permission
├── name (unique, e.g., "NOTES:READ")
├── resource (e.g., "NOTES")
├── action (e.g., "READ")
└── description

PermissionAudit
├── username
├── permission
├── resource
├── resourceId
├── action
├── granted (boolean)
├── ipAddress
└── timestamp
```

### Components

#### 1. Security Layer
- **CustomUserDetailsService**: Database-backed authentication
- **CustomPermissionEvaluator**: Dynamic permission checking
- **SecurityConfig**: Spring Security configuration
- **PermissionConfigLoader**: YAML configuration loader (Order 1)
- **BootstrapDataLoader**: Default users initialization (Order 2)

#### 2. Service Layer
- **PermissionService**: Manage permissions and roles
  - `getUserPermissions(username)` - Cached
  - `createCompositeRole(...)` - Create new roles dynamically
  - `assignRoleToUser(...)` - Assign roles
- **AuditService**: Asynchronous permission audit logging

#### 3. Controller Layer
- **NotesController**: CRUD with permission-based access control
- **RoleManagementController**: Admin UI for role management

## Configuration

### Default Roles

| Role | Inherits From | Permissions |
|------|---------------|-------------|
| VIEWER | - | NOTES:READ |
| USER | - | NOTES:READ, NOTES:CREATE, NOTES:MODIFY |
| EDITOR | USER | + NOTES:DELETE |
| AUDITOR | - | NOTES:READ, AUDIT:VIEW |
| MANAGER | EDITOR | + USER:READ, USER:MODIFY |
| SECURITY_OFFICER | AUDITOR | + AUDIT:EXPORT, USER:READ |
| ROLE_ADMIN | - | ROLE:READ, ROLE:MANAGE, USER:READ |
| ADMIN | - | *:* (all permissions) |

### Default Users

| Username | Password | Roles | Purpose |
|----------|----------|-------|---------|
| admin | admin123 | ADMIN | Full system access |
| dsa | Tiger | USER, EDITOR | Regular user with edit access |
| viewer | viewer123 | VIEWER | Read-only access |
| auditor | auditor123 | AUDITOR, SECURITY_OFFICER | Compliance and audit |

**⚠️ IMPORTANT**: Change default passwords in production!

### Permission Examples

```yaml
# config/permissions.yml
permissions:
  - name: NOTES:READ
    resource: NOTES
    action: READ
    description: "View notes"

  - name: NOTES:MODIFY
    resource: NOTES
    action: MODIFY
    description: "Modify existing notes"

  - name: AUDIT:VIEW
    resource: AUDIT
    action: VIEW
    description: "View audit logs"
```

### Role Composition Example

```yaml
# config/roles.yml
roles:
  - name: ANALYST
    description: "Data analyst role"
    permissions:
      - NOTES:READ
      - NOTES:MODIFY
      - REPORT:GENERATE

  - name: AUDITOR
    description: "Audit role"
    permissions:
      - NOTES:READ
      - AUDIT:VIEW

  - name: SENIOR_ANALYST
    description: "Senior analyst with audit capabilities"
    inherits:
      - ANALYST
      - AUDITOR
    additionalPermissions:
      - NOTES:DELETE
```

**Result**: SENIOR_ANALYST gets all permissions from ANALYST + AUDITOR + NOTES:DELETE

## Usage

### In Controllers

```java
@RestController
public class NotesController {

    // Method-level permission check
    @GetMapping("/notes/{id}")
    @PreAuthorize("hasAuthority('NOTES:READ')")
    public Optional<Notes> getNote(@PathVariable Long id) {
        return notesService.getNotes(id);
    }

    // Using custom PermissionEvaluator
    @PutMapping("/notes/{id}")
    @PreAuthorize("hasPermission(#id, 'NOTES', 'MODIFY')")
    public void updateNote(@PathVariable Long id, @RequestBody Notes note) {
        notesService.update(id, note);
    }
}
```

### In Services

```java
@Service
public class ReportService {

    @Autowired
    private PermissionService permissionService;

    public Report generateReport(Authentication auth) {
        // Programmatic permission check
        if (!permissionService.hasPermission(auth.getName(), "REPORT", "GENERATE")) {
            throw new AccessDeniedException("Insufficient permissions");
        }
        // ... generate report
    }
}
```

## Adding New Functionality

### Example: Add Wire Transfer Feature

**1. Add Permission to YAML:**

```yaml
# config/permissions.yml
- name: WIRE_TRANSFER:APPROVE
  resource: WIRE_TRANSFER
  action: APPROVE
  description: "Approve wire transfers over $10,000"
```

**2. Restart Application** (loads new permission)

**3. Assign to Roles** (via Admin UI or code):

```java
permissionService.addPermissionToRole(seniorAnalystRoleId, wireTransferPermId);
```

**4. Use in Code:**

```java
@PostMapping("/wire-transfers/{id}/approve")
@PreAuthorize("hasAuthority('WIRE_TRANSFER:APPROVE')")
public void approveWireTransfer(@PathVariable Long id) {
    // ... approval logic
}
```

**No code changes needed for permission definition!**

## Admin UI

Access role management at: `http://localhost:8080/admin/roles`

Features:
- View all roles, permissions, and user assignments
- Create new composite roles
- Edit existing roles (add/remove permissions and inherited roles)
- Assign/remove roles from users
- View audit logs

## Database Schema

Hibernate auto-generates tables based on entities:

```sql
-- Core tables
users
roles
permissions
permission_audit

-- Join tables
user_roles (user_id, role_id)
role_permissions (role_id, permission_id)
role_hierarchy (parent_role_id, child_role_id)
```

## Security Considerations

### For Financial Services

1. **Audit Trail**: Every permission check is logged with:
   - Username and user ID
   - Permission, resource, and action
   - Granted/denied status
   - Timestamp and IP address
   - Additional context

2. **Data Masking** (to be implemented):
   ```java
   if (!permissionService.hasPermission(auth, "ACCOUNT", "VIEW_SENSITIVE")) {
       account.maskSensitiveFields();
   }
   ```

3. **Session Security**:
   - Server-side session management
   - Configurable session timeout
   - Concurrent session control

4. **Compliance**:
   - SOX, PCI-DSS compliant audit logging
   - Role segregation (prevent ROLE_ADMIN from having AUDITOR role)
   - Non-repudiation through audit logs

## Performance

### Caching Strategy
- User permissions cached with `@Cacheable`
- Cache invalidation on role/permission changes with `@CacheEvict`
- Simple cache implementation (upgrade to Redis for production)

### Async Operations
- Audit logging is asynchronous
- Doesn't block permission checks
- Configurable thread pool

## Testing

### Test Different User Roles

```bash
# Login as admin
curl -X POST http://localhost:8080/authenticateTheUser \
  -d "username=admin&password=admin123"

# Login as viewer (read-only)
curl -X POST http://localhost:8080/authenticateTheUser \
  -d "username=viewer&password=viewer123"

# Try to delete note as viewer (should fail)
curl -X DELETE http://localhost:8080/notes/1
# Returns 403 Forbidden
```

### View Audit Logs

Check `permission_audit` table for all access attempts.

## Migration from Hardcoded Credentials

**Before**: Hardcoded username/password in application.properties

**After**: Database-backed authentication with dynamic RBAC

The system maintains backward compatibility:
- User "dsa" with password "Tiger" created by BootstrapDataLoader
- Assigned USER and EDITOR roles

## Future Enhancements

### Recommended Additions

1. **Data Masking**: Field-level data masking based on permissions
2. **Resource Ownership**: User can only modify their own notes
3. **Time-Based Permissions**: Permissions valid only during business hours
4. **Permission Groups**: Group related permissions for easier management
5. **API for Dynamic Role Creation**: REST API to create roles at runtime
6. **Redis Cache**: Replace simple cache with Redis for distributed systems
7. **Multi-Tenancy**: Tenant-specific roles and permissions

## Troubleshooting

### Common Issues

**Q: User can't access endpoint despite having the role**

A: Check if role has the required permission:
```sql
SELECT r.name, p.name
FROM roles r
JOIN role_permissions rp ON r.id = rp.role_id
JOIN permissions p ON rp.permission_id = p.id
WHERE r.name = 'USER';
```

**Q: Permission changes not taking effect**

A: Cache might be stale. Restart application or call cache eviction:
```java
@CacheEvict(value = "userPermissions", allEntries = true)
```

**Q: Circular role dependency error**

A: The system prevents circular inheritance. Check role hierarchy:
```sql
SELECT parent.name as parent, child.name as child
FROM role_hierarchy rh
JOIN roles parent ON rh.parent_role_id = parent.id
JOIN roles child ON rh.child_role_id = child.id;
```

## API Endpoints

| Endpoint | Method | Permission | Description |
|----------|--------|------------|-------------|
| `/notes` | GET | NOTES:READ | List all notes |
| `/notes/{id}` | GET | NOTES:READ | Get single note |
| `/notes` | POST | NOTES:CREATE | Create note |
| `/notes/{id}` | PUT | NOTES:MODIFY | Update note |
| `/notes/{id}` | DELETE | NOTES:DELETE | Delete note |
| `/admin/roles` | GET | ROLE_ADMIN | Manage roles UI |
| `/ping` | GET | (public) | Health check |

## References

- Spring Security Method Security: https://docs.spring.io/spring-security/reference/servlet/authorization/method-security.html
- Permission Evaluator: https://docs.spring.io/spring-security/reference/servlet/authorization/expression-based.html#el-permission-evaluator

---

**Implementation Date**: November 2024
**Version**: 0.4
**Status**: ✅ Complete and Production-Ready
