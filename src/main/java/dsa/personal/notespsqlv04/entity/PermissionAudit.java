package dsa.personal.notespsqlv04.entity;

import jakarta.persistence.*;
import java.sql.Timestamp;

/**
 * Audit log entity for tracking permission checks.
 * Essential for compliance in financial services applications.
 */
@Entity
@Table(name = "permission_audit", indexes = {
    @Index(name = "idx_audit_user", columnList = "user_id"),
    @Index(name = "idx_audit_timestamp", columnList = "timestamp"),
    @Index(name = "idx_audit_permission", columnList = "permission")
})
public class PermissionAudit {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "user_id")
    private Long userId;

    @Column(name = "username", length = 50)
    private String username;

    @Column(length = 100)
    private String permission;  // e.g., "ACCOUNT:READ"

    @Column(length = 100)
    private String resource;    // e.g., "ACCOUNT"

    @Column(name = "resource_id", length = 100)
    private String resourceId;  // e.g., "123" (the specific resource accessed)

    @Column(length = 50)
    private String action;      // e.g., "READ"

    @Column(nullable = false)
    private Boolean granted;    // true if permission was granted, false if denied

    @Column(name = "ip_address", length = 45)
    private String ipAddress;

    @Column(name = "user_agent", length = 255)
    private String userAgent;

    @Column(columnDefinition = "TEXT")
    private String details;     // Additional context

    @Column(nullable = false)
    private Timestamp timestamp;

    // Constructors
    public PermissionAudit() {
        this.timestamp = new Timestamp(System.currentTimeMillis());
    }

    public PermissionAudit(Long userId, String username, String permission,
                          String resource, String action, Boolean granted) {
        this.userId = userId;
        this.username = username;
        this.permission = permission;
        this.resource = resource;
        this.action = action;
        this.granted = granted;
        this.timestamp = new Timestamp(System.currentTimeMillis());
    }

    @PrePersist
    protected void onCreate() {
        if (timestamp == null) {
            timestamp = new Timestamp(System.currentTimeMillis());
        }
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPermission() {
        return permission;
    }

    public void setPermission(String permission) {
        this.permission = permission;
    }

    public String getResource() {
        return resource;
    }

    public void setResource(String resource) {
        this.resource = resource;
    }

    public String getResourceId() {
        return resourceId;
    }

    public void setResourceId(String resourceId) {
        this.resourceId = resourceId;
    }

    public String getAction() {
        return action;
    }

    public void setAction(String action) {
        this.action = action;
    }

    public Boolean getGranted() {
        return granted;
    }

    public void setGranted(Boolean granted) {
        this.granted = granted;
    }

    public String getIpAddress() {
        return ipAddress;
    }

    public void setIpAddress(String ipAddress) {
        this.ipAddress = ipAddress;
    }

    public String getUserAgent() {
        return userAgent;
    }

    public void setUserAgent(String userAgent) {
        this.userAgent = userAgent;
    }

    public String getDetails() {
        return details;
    }

    public void setDetails(String details) {
        this.details = details;
    }

    public Timestamp getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Timestamp timestamp) {
        this.timestamp = timestamp;
    }

    @Override
    public String toString() {
        return "PermissionAudit{" +
                "id=" + id +
                ", username='" + username + '\'' +
                ", permission='" + permission + '\'' +
                ", resource='" + resource + '\'' +
                ", resourceId='" + resourceId + '\'' +
                ", action='" + action + '\'' +
                ", granted=" + granted +
                ", timestamp=" + timestamp +
                '}';
    }
}
