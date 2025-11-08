package dsa.personal.notespsqlv04.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import dsa.personal.notespsqlv04.dto.PermissionConfig;
import dsa.personal.notespsqlv04.dto.PermissionDTO;
import dsa.personal.notespsqlv04.dto.RoleConfig;
import dsa.personal.notespsqlv04.dto.RoleDTO;
import dsa.personal.notespsqlv04.entity.Permission;
import dsa.personal.notespsqlv04.entity.Role;
import dsa.personal.notespsqlv04.repository.PermissionRepository;
import dsa.personal.notespsqlv04.repository.RoleRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.core.annotation.Order;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

/**
 * Loads permission and role configurations from YAML files on application startup.
 * This allows external configuration of permissions and roles without code changes.
 * Runs before BootstrapDataLoader (order 1).
 */
@Component
@Order(1)
public class PermissionConfigLoader implements ApplicationRunner {

    private static final Logger logger = LoggerFactory.getLogger(PermissionConfigLoader.class);

    @Autowired
    private PermissionRepository permissionRepository;

    @Autowired
    private RoleRepository roleRepository;

    @Value("${security.permissions.config-path:classpath:config/permissions.yml}")
    private Resource permissionsConfig;

    @Value("${security.roles.config-path:classpath:config/roles.yml}")
    private Resource rolesConfig;

    @Override
    @Transactional
    public void run(ApplicationArguments args) throws Exception {
        logger.info("Loading permission and role configurations...");

        try {
            loadPermissions();
            loadRoles();
            logger.info("Successfully loaded permission and role configurations");
        } catch (Exception e) {
            logger.warn("Failed to load configurations from YAML files: {}. Using defaults.", e.getMessage());
            // Continue startup - bootstrap data will create minimal setup
        }
    }

    /**
     * Load permissions from YAML configuration
     */
    private void loadPermissions() throws IOException {
        if (!permissionsConfig.exists()) {
            logger.warn("Permissions config file not found at: {}", permissionsConfig);
            return;
        }

        logger.info("Loading permissions from: {}", permissionsConfig);

        ObjectMapper mapper = new ObjectMapper(new YAMLFactory());
        PermissionConfig config = mapper.readValue(
                permissionsConfig.getInputStream(),
                PermissionConfig.class
        );

        if (config.getPermissions() == null || config.getPermissions().isEmpty()) {
            logger.warn("No permissions defined in config file");
            return;
        }

        int loadedCount = 0;
        for (PermissionDTO dto : config.getPermissions()) {
            try {
                permissionRepository.findByName(dto.getName())
                        .orElseGet(() -> {
                            Permission perm = new Permission();
                            perm.setName(dto.getName());
                            perm.setResource(dto.getResource());
                            perm.setAction(dto.getAction());
                            perm.setDescription(dto.getDescription());
                            return permissionRepository.save(perm);
                        });
                loadedCount++;
            } catch (Exception e) {
                logger.error("Failed to load permission: " + dto.getName(), e);
            }
        }

        logger.info("Loaded {} permissions from configuration", loadedCount);
    }

    /**
     * Load roles from YAML configuration
     */
    private void loadRoles() throws IOException {
        if (!rolesConfig.exists()) {
            logger.warn("Roles config file not found at: {}", rolesConfig);
            return;
        }

        logger.info("Loading roles from: {}", rolesConfig);

        ObjectMapper mapper = new ObjectMapper(new YAMLFactory());
        RoleConfig config = mapper.readValue(
                rolesConfig.getInputStream(),
                RoleConfig.class
        );

        if (config.getRoles() == null || config.getRoles().isEmpty()) {
            logger.warn("No roles defined in config file");
            return;
        }

        // Load roles in multiple passes to handle dependencies
        int loadedCount = 0;
        for (RoleDTO dto : config.getRoles()) {
            try {
                roleRepository.findByName(dto.getName())
                        .orElseGet(() -> createRoleFromConfig(dto));
                loadedCount++;
            } catch (Exception e) {
                logger.error("Failed to load role: " + dto.getName(), e);
            }
        }

        logger.info("Loaded {} roles from configuration", loadedCount);
    }

    /**
     * Create a role from DTO configuration
     */
    private Role createRoleFromConfig(RoleDTO dto) {
        logger.debug("Creating role: {}", dto.getName());

        Role role = new Role();
        role.setName(dto.getName());
        role.setDescription(dto.getDescription());

        // Add direct permissions
        if (dto.getPermissions() != null && !dto.getPermissions().isEmpty()) {
            Set<Permission> perms = permissionRepository.findByNameIn(dto.getPermissions());
            if (perms.size() != dto.getPermissions().size()) {
                logger.warn("Some permissions not found for role: {}. Expected: {}, Found: {}",
                        dto.getName(), dto.getPermissions().size(), perms.size());
            }
            role.getPermissions().addAll(perms);
        }

        // Add additional permissions (used with inheritance)
        if (dto.getAdditionalPermissions() != null && !dto.getAdditionalPermissions().isEmpty()) {
            Set<Permission> additionalPerms = permissionRepository.findByNameIn(dto.getAdditionalPermissions());
            role.getPermissions().addAll(additionalPerms);
        }

        // Save role first
        role = roleRepository.save(role);

        // Add inherited roles (after role is persisted)
        if (dto.getInherits() != null && !dto.getInherits().isEmpty()) {
            Set<Role> inheritedRoles = new HashSet<>();
            for (String inheritedRoleName : dto.getInherits()) {
                roleRepository.findByName(inheritedRoleName).ifPresentOrElse(
                        inheritedRoles::add,
                        () -> logger.warn("Inherited role not found: {} for role: {}",
                                inheritedRoleName, dto.getName())
                );
            }
            role.getChildRoles().addAll(inheritedRoles);
            role = roleRepository.save(role);
        }

        logger.info("Created role: {} with {} direct permissions and {} inherited roles",
                dto.getName(), role.getPermissions().size(), role.getChildRoles().size());

        return role;
    }
}
