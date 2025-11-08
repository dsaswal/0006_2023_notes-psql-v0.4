package dsa.personal.notespsqlv04.dto;

import java.util.List;

/**
 * Root configuration class for permissions YAML file
 */
public class PermissionConfig {
    private List<PermissionDTO> permissions;

    public PermissionConfig() {
    }

    public List<PermissionDTO> getPermissions() {
        return permissions;
    }

    public void setPermissions(List<PermissionDTO> permissions) {
        this.permissions = permissions;
    }
}
