package dsa.personal.notespsqlv04.controller;

import dsa.personal.notespsqlv04.entity.Permission;
import dsa.personal.notespsqlv04.entity.Role;
import dsa.personal.notespsqlv04.entity.User;
import dsa.personal.notespsqlv04.repository.PermissionRepository;
import dsa.personal.notespsqlv04.repository.RoleRepository;
import dsa.personal.notespsqlv04.repository.UserRepository;
import dsa.personal.notespsqlv04.service.PermissionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.HashSet;
import java.util.Set;

/**
 * Controller for role and permission management.
 * Requires ADMIN role for all operations.
 */
@Controller
@RequestMapping("/admin/roles")
@PreAuthorize("hasRole('ADMIN')")
public class RoleManagementController {

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private PermissionRepository permissionRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PermissionService permissionService;

    /**
     * List all roles and permissions
     */
    @GetMapping
    public String listRoles(Model model) {
        model.addAttribute("roles", roleRepository.findAll());
        model.addAttribute("permissions", permissionRepository.findAll());
        model.addAttribute("users", userRepository.findAll());
        return "admin/roles";
    }

    /**
     * Show create role form
     */
    @GetMapping("/create")
    public String showCreateForm(Model model) {
        model.addAttribute("permissions", permissionRepository.findAll());
        model.addAttribute("roles", roleRepository.findAll());
        return "admin/role-create";
    }

    /**
     * Create a new role
     */
    @PostMapping("/create")
    public String createRole(
            @RequestParam String name,
            @RequestParam String description,
            @RequestParam(required = false) Set<Long> permissionIds,
            @RequestParam(required = false) Set<Long> inheritedRoleIds,
            RedirectAttributes redirectAttributes) {

        try {
            if (roleRepository.existsByName(name)) {
                redirectAttributes.addFlashAttribute("error", "Role already exists: " + name);
                return "redirect:/admin/roles/create";
            }

            Role newRole = new Role();
            newRole.setName(name);
            newRole.setDescription(description);

            // Add permissions
            if (permissionIds != null && !permissionIds.isEmpty()) {
                Set<Permission> permissions = new HashSet<>();
                for (Long permId : permissionIds) {
                    permissionRepository.findById(permId).ifPresent(permissions::add);
                }
                newRole.setPermissions(permissions);
            }

            // Add inherited roles
            if (inheritedRoleIds != null && !inheritedRoleIds.isEmpty()) {
                Set<Role> inheritedRoles = new HashSet<>();
                for (Long roleId : inheritedRoleIds) {
                    roleRepository.findById(roleId).ifPresent(inheritedRoles::add);
                }
                newRole.setChildRoles(inheritedRoles);
            }

            roleRepository.save(newRole);
            redirectAttributes.addFlashAttribute("success", "Role created successfully: " + name);

        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Failed to create role: " + e.getMessage());
        }

        return "redirect:/admin/roles";
    }

    /**
     * Show edit role form
     */
    @GetMapping("/{id}/edit")
    public String showEditForm(@PathVariable Long id, Model model) {
        Role role = roleRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Role not found: " + id));

        model.addAttribute("role", role);
        model.addAttribute("permissions", permissionRepository.findAll());
        model.addAttribute("allRoles", roleRepository.findAll());

        return "admin/role-edit";
    }

    /**
     * Update a role
     */
    @PostMapping("/{id}/edit")
    public String updateRole(
            @PathVariable Long id,
            @RequestParam String description,
            @RequestParam(required = false) Set<Long> permissionIds,
            @RequestParam(required = false) Set<Long> inheritedRoleIds,
            RedirectAttributes redirectAttributes) {

        try {
            Role role = roleRepository.findById(id)
                    .orElseThrow(() -> new IllegalArgumentException("Role not found: " + id));

            role.setDescription(description);

            // Update permissions
            role.getPermissions().clear();
            if (permissionIds != null && !permissionIds.isEmpty()) {
                for (Long permId : permissionIds) {
                    permissionRepository.findById(permId).ifPresent(role.getPermissions()::add);
                }
            }

            // Update inherited roles
            role.getChildRoles().clear();
            if (inheritedRoleIds != null && !inheritedRoleIds.isEmpty()) {
                for (Long roleId : inheritedRoleIds) {
                    if (!roleId.equals(id)) {  // Prevent self-reference
                        roleRepository.findById(roleId).ifPresent(role.getChildRoles()::add);
                    }
                }
            }

            roleRepository.save(role);
            redirectAttributes.addFlashAttribute("success", "Role updated successfully");

        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Failed to update role: " + e.getMessage());
        }

        return "redirect:/admin/roles";
    }

    /**
     * Delete a role
     */
    @PostMapping("/{id}/delete")
    public String deleteRole(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            Role role = roleRepository.findById(id)
                    .orElseThrow(() -> new IllegalArgumentException("Role not found: " + id));

            if (role.getIsSystem()) {
                redirectAttributes.addFlashAttribute("error", "Cannot delete system role: " + role.getName());
                return "redirect:/admin/roles";
            }

            roleRepository.delete(role);
            redirectAttributes.addFlashAttribute("success", "Role deleted successfully");

        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Failed to delete role: " + e.getMessage());
        }

        return "redirect:/admin/roles";
    }

    /**
     * Assign role to user
     */
    @PostMapping("/assign")
    public String assignRole(
            @RequestParam Long userId,
            @RequestParam Long roleId,
            RedirectAttributes redirectAttributes) {

        try {
            User user = userRepository.findById(userId)
                    .orElseThrow(() -> new IllegalArgumentException("User not found"));
            Role role = roleRepository.findById(roleId)
                    .orElseThrow(() -> new IllegalArgumentException("Role not found"));

            user.getRoles().add(role);
            userRepository.save(user);

            redirectAttributes.addFlashAttribute("success",
                    "Assigned role " + role.getName() + " to user " + user.getUsername());

        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Failed to assign role: " + e.getMessage());
        }

        return "redirect:/admin/roles";
    }

    /**
     * Remove role from user
     */
    @PostMapping("/remove")
    public String removeRole(
            @RequestParam Long userId,
            @RequestParam Long roleId,
            RedirectAttributes redirectAttributes) {

        try {
            User user = userRepository.findById(userId)
                    .orElseThrow(() -> new IllegalArgumentException("User not found"));
            Role role = roleRepository.findById(roleId)
                    .orElseThrow(() -> new IllegalArgumentException("Role not found"));

            user.getRoles().remove(role);
            userRepository.save(user);

            redirectAttributes.addFlashAttribute("success",
                    "Removed role " + role.getName() + " from user " + user.getUsername());

        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Failed to remove role: " + e.getMessage());
        }

        return "redirect:/admin/roles";
    }
}
