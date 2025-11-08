package dsa.personal.notespsqlv04.dto;

import java.util.List;

/**
 * Root configuration class for roles YAML file
 */
public class RoleConfig {
    private List<RoleDTO> roles;

    public RoleConfig() {
    }

    public List<RoleDTO> getRoles() {
        return roles;
    }

    public void setRoles(List<RoleDTO> roles) {
        this.roles = roles;
    }
}
