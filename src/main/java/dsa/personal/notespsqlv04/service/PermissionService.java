package dsa.personal.notespsqlv04.service;

import dsa.personal.notespsqlv04.entity.Permission;
import dsa.personal.notespsqlv04.entity.Role;
import dsa.personal.notespsqlv04.entity.User;
import dsa.personal.notespsqlv04.repository.PermissionRepository;
import dsa.personal.notespsqlv04.repository.RoleRepository;
import dsa.personal.notespsqlv04.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.Set;

/**
 * Service for managing permissions and roles.
 * Supports dynamic role composition and permission evaluation.
 */
@Service
public class PermissionService {

    private static final Logger logger = LoggerFactory.getLogger(PermissionService.class);

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private PermissionRepository permissionRepository;

    /**
     * Get all permissions for a user (cached for performance)
     */
    @Cacheable(value = "userPermissions", key = "#username")
    public Set<Permission> getUserPermissions(String username) {
        logger.debug("Loading permissions for user: {}", username);

        User user = userRepository.findByUsernameWithRoles(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found: " + username));

        Set<Permission> permissions = new HashSet<>();

        // Get permissions from all user roles (including inherited)
        for (Role role : user.getRoles()) {
            permissions.addAll(role.getAllPermissions());
        }

        logger.debug("User {} has {} permissions", username, permissions.size());
        return permissions;
    }

    /**
     * Check if user has a specific permission
     */
    public boolean hasPermission(String username, String resource, String action) {
        Set<Permission> userPermissions = getUserPermissions(username);

        return userPermissions.stream()
                .anyMatch(p -> p.matches(resource, action));
    }

    /**
     * Create a new composite role
     */
    @Transactional
    @CacheEvict(value = "userPermissions", allEntries = true)
    public Role createCompositeRole(String name, String description,
                                   Set<String> inheritedRoleNames,
                                   Set<String> additionalPermissionNames) {
        logger.info("Creating composite role: {}", name);

        if (roleRepository.existsByName(name)) {
            throw new IllegalArgumentException("Role already exists: " + name);
        }

        Role newRole = new Role();
        newRole.setName(name);
        newRole.setDescription(description);

        // Add inherited roles
        if (inheritedRoleNames != null && !inheritedRoleNames.isEmpty()) {
            Set<Role> inheritedRoles = roleRepository.findByNameIn(inheritedRoleNames);
            if (inheritedRoles.size() != inheritedRoleNames.size()) {
                throw new IllegalArgumentException("Some inherited roles not found");
            }
            newRole.getChildRoles().addAll(inheritedRoles);
        }

        // Add additional permissions
        if (additionalPermissionNames != null && !additionalPermissionNames.isEmpty()) {
            Set<Permission> additionalPerms = permissionRepository.findByNameIn(additionalPermissionNames);
            if (additionalPerms.size() != additionalPermissionNames.size()) {
                throw new IllegalArgumentException("Some permissions not found");
            }
            newRole.getPermissions().addAll(additionalPerms);
        }

        Role savedRole = roleRepository.save(newRole);
        logger.info("Created composite role: {} with {} direct permissions and {} inherited roles",
                name, newRole.getPermissions().size(), newRole.getChildRoles().size());

        return savedRole;
    }

    /**
     * Add permission to role
     */
    @Transactional
    @CacheEvict(value = "userPermissions", allEntries = true)
    public void addPermissionToRole(Long roleId, Long permissionId) {
        Role role = roleRepository.findById(roleId)
                .orElseThrow(() -> new IllegalArgumentException("Role not found: " + roleId));

        Permission permission = permissionRepository.findById(permissionId)
                .orElseThrow(() -> new IllegalArgumentException("Permission not found: " + permissionId));

        role.getPermissions().add(permission);
        roleRepository.save(role);

        logger.info("Added permission {} to role {}", permission.getName(), role.getName());
    }

    /**
     * Remove permission from role
     */
    @Transactional
    @CacheEvict(value = "userPermissions", allEntries = true)
    public void removePermissionFromRole(Long roleId, Long permissionId) {
        Role role = roleRepository.findById(roleId)
                .orElseThrow(() -> new IllegalArgumentException("Role not found: " + roleId));

        Permission permission = permissionRepository.findById(permissionId)
                .orElseThrow(() -> new IllegalArgumentException("Permission not found: " + permissionId));

        role.getPermissions().remove(permission);
        roleRepository.save(role);

        logger.info("Removed permission {} from role {}", permission.getName(), role.getName());
    }

    /**
     * Add inherited role to a parent role
     */
    @Transactional
    @CacheEvict(value = "userPermissions", allEntries = true)
    public void addInheritedRole(Long parentRoleId, Long childRoleId) {
        Role parentRole = roleRepository.findById(parentRoleId)
                .orElseThrow(() -> new IllegalArgumentException("Parent role not found: " + parentRoleId));

        Role childRole = roleRepository.findById(childRoleId)
                .orElseThrow(() -> new IllegalArgumentException("Child role not found: " + childRoleId));

        // Prevent circular inheritance
        if (wouldCreateCircularDependency(parentRole, childRole)) {
            throw new IllegalArgumentException("Adding this role would create a circular dependency");
        }

        parentRole.getChildRoles().add(childRole);
        roleRepository.save(parentRole);

        logger.info("Added inherited role {} to {}", childRole.getName(), parentRole.getName());
    }

    /**
     * Check for circular dependency in role hierarchy
     */
    private boolean wouldCreateCircularDependency(Role parent, Role potentialChild) {
        if (parent.equals(potentialChild)) {
            return true;
        }

        for (Role child : parent.getChildRoles()) {
            if (wouldCreateCircularDependency(child, potentialChild)) {
                return true;
            }
        }

        return false;
    }

    /**
     * Assign role to user
     */
    @Transactional
    @CacheEvict(value = "userPermissions", key = "#username")
    public void assignRoleToUser(String username, String roleName) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found: " + username));

        Role role = roleRepository.findByName(roleName)
                .orElseThrow(() -> new IllegalArgumentException("Role not found: " + roleName));

        user.getRoles().add(role);
        userRepository.save(user);

        logger.info("Assigned role {} to user {}", roleName, username);
    }

    /**
     * Remove role from user
     */
    @Transactional
    @CacheEvict(value = "userPermissions", key = "#username")
    public void removeRoleFromUser(String username, String roleName) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found: " + username));

        Role role = roleRepository.findByName(roleName)
                .orElseThrow(() -> new IllegalArgumentException("Role not found: " + roleName));

        user.getRoles().remove(role);
        userRepository.save(user);

        logger.info("Removed role {} from user {}", roleName, username);
    }

    /**
     * Get all roles
     */
    public Set<Role> getAllRoles() {
        return new HashSet<>(roleRepository.findAll());
    }

    /**
     * Get all permissions
     */
    public Set<Permission> getAllPermissions() {
        return new HashSet<>(permissionRepository.findAll());
    }
}
