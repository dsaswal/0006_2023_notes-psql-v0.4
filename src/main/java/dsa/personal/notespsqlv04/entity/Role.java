package dsa.personal.notespsqlv04.entity;

import jakarta.persistence.*;
import java.util.HashSet;
import java.util.Set;
import java.util.Objects;

/**
 * Role entity representing a collection of permissions.
 * Supports role composition through the role hierarchy.
 */
@Entity
@Table(name = "roles")
public class Role {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false, length = 100)
    private String name;  // e.g., "ANALYST", "SENIOR_ANALYST"

    @Column(columnDefinition = "TEXT")
    private String description;

    @Column(name = "is_system")
    private Boolean isSystem = false;  // Prevent deletion of system roles

    // Direct permissions assigned to this role
    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
        name = "role_permissions",
        joinColumns = @JoinColumn(name = "role_id"),
        inverseJoinColumns = @JoinColumn(name = "permission_id")
    )
    private Set<Permission> permissions = new HashSet<>();

    // Role composition: roles that this role inherits from
    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
        name = "role_hierarchy",
        joinColumns = @JoinColumn(name = "parent_role_id"),
        inverseJoinColumns = @JoinColumn(name = "child_role_id")
    )
    private Set<Role> childRoles = new HashSet<>();

    // Constructors
    public Role() {
    }

    public Role(String name, String description) {
        this.name = name;
        this.description = description;
    }

    public Role(String name, String description, Boolean isSystem) {
        this.name = name;
        this.description = description;
        this.isSystem = isSystem;
    }

    /**
     * Get all permissions including inherited permissions from child roles.
     * This recursively collects permissions from the entire role hierarchy.
     */
    public Set<Permission> getAllPermissions() {
        Set<Permission> allPermissions = new HashSet<>(permissions);

        // Recursively add permissions from child roles
        for (Role childRole : childRoles) {
            allPermissions.addAll(childRole.getAllPermissions());
        }

        return allPermissions;
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

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Boolean getIsSystem() {
        return isSystem;
    }

    public void setIsSystem(Boolean isSystem) {
        this.isSystem = isSystem;
    }

    public Set<Permission> getPermissions() {
        return permissions;
    }

    public void setPermissions(Set<Permission> permissions) {
        this.permissions = permissions;
    }

    public Set<Role> getChildRoles() {
        return childRoles;
    }

    public void setChildRoles(Set<Role> childRoles) {
        this.childRoles = childRoles;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Role role = (Role) o;
        return Objects.equals(name, role.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name);
    }

    @Override
    public String toString() {
        return "Role{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", isSystem=" + isSystem +
                '}';
    }
}
