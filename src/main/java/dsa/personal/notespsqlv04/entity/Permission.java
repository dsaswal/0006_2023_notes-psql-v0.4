package dsa.personal.notespsqlv04.entity;

import jakarta.persistence.*;
import java.util.Objects;

/**
 * Permission entity representing atomic permissions in the RBAC system.
 * Format: RESOURCE:ACTION (e.g., "ACCOUNT:READ", "TRANSACTION:MODIFY")
 */
@Entity
@Table(name = "permissions")
public class Permission {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false, length = 100)
    private String name;  // e.g., "ACCOUNT:READ"

    @Column(nullable = false, length = 50)
    private String resource;  // e.g., "ACCOUNT"

    @Column(nullable = false, length = 50)
    private String action;    // e.g., "READ", "MODIFY", "DELETE"

    @Column(columnDefinition = "TEXT")
    private String description;

    // Constructors
    public Permission() {
    }

    public Permission(String name, String resource, String action, String description) {
        this.name = name;
        this.resource = resource;
        this.action = action;
        this.description = description;
    }

    /**
     * Check if this permission matches the given resource and action.
     * Supports wildcard matching with "*"
     */
    public boolean matches(String resource, String action) {
        boolean resourceMatches = "*".equals(this.resource) || this.resource.equals(resource);
        boolean actionMatches = "*".equals(this.action) || this.action.equals(action);
        return resourceMatches && actionMatches;
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getResource() {
        return resource;
    }

    public void setResource(String resource) {
        this.resource = resource;
    }

    public String getAction() {
        return action;
    }

    public void setAction(String action) {
        this.action = action;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Permission that = (Permission) o;
        return Objects.equals(name, that.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name);
    }

    @Override
    public String toString() {
        return "Permission{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", resource='" + resource + '\'' +
                ", action='" + action + '\'' +
                '}';
    }
}
