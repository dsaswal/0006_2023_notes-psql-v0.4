package dsa.personal.notespsqlv04.dto;

import java.util.Set;

/**
 * DTO for loading role configuration from YAML
 */
public class RoleDTO {
    private String name;
    private String description;
    private Set<String> permissions;
    private Set<String> inherits;
    private Set<String> additionalPermissions;

    public RoleDTO() {
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

    public Set<String> getPermissions() {
        return permissions;
    }

    public void setPermissions(Set<String> permissions) {
        this.permissions = permissions;
    }

    public Set<String> getInherits() {
        return inherits;
    }

    public void setInherits(Set<String> inherits) {
        this.inherits = inherits;
    }

    public Set<String> getAdditionalPermissions() {
        return additionalPermissions;
    }

    public void setAdditionalPermissions(Set<String> additionalPermissions) {
        this.additionalPermissions = additionalPermissions;
    }
}
