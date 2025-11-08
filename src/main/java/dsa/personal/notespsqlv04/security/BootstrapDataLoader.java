package dsa.personal.notespsqlv04.security;

import dsa.personal.notespsqlv04.entity.Role;
import dsa.personal.notespsqlv04.entity.User;
import dsa.personal.notespsqlv04.repository.RoleRepository;
import dsa.personal.notespsqlv04.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.annotation.Order;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.Set;

/**
 * Bootstrap data loader for creating initial users and roles.
 * Runs after PermissionConfigLoader to ensure roles are loaded.
 */
@Component
@Order(2)
public class BootstrapDataLoader implements CommandLineRunner {

    private static final Logger logger = LoggerFactory.getLogger(BootstrapDataLoader.class);

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Override
    @Transactional
    public void run(String... args) throws Exception {
        logger.info("Loading bootstrap data...");

        try {
            createDefaultUsers();
            logger.info("Bootstrap data loaded successfully");
        } catch (Exception e) {
            logger.error("Failed to load bootstrap data", e);
        }
    }

    /**
     * Create default users if they don't exist
     */
    private void createDefaultUsers() {
        // Create admin user if not exists
        if (!userRepository.existsByUsername("admin")) {
            User admin = new User();
            admin.setUsername("admin");
            admin.setPassword(passwordEncoder.encode("admin123"));  // Change in production!
            admin.setEmail("admin@example.com");
            admin.setEnabled(true);

            // Assign ADMIN role
            roleRepository.findByName("ADMIN").ifPresent(role -> {
                Set<Role> roles = new HashSet<>();
                roles.add(role);
                admin.setRoles(roles);
            });

            userRepository.save(admin);
            logger.info("Created default admin user (username: admin, password: admin123)");
        }

        // Create regular user if not exists (using existing credentials)
        if (!userRepository.existsByUsername("dsa")) {
            User user = new User();
            user.setUsername("dsa");
            user.setPassword(passwordEncoder.encode("Tiger"));
            user.setEmail("dsa@example.com");
            user.setEnabled(true);

            // Assign USER and EDITOR roles
            Set<Role> roles = new HashSet<>();
            roleRepository.findByName("USER").ifPresent(roles::add);
            roleRepository.findByName("EDITOR").ifPresent(roles::add);
            user.setRoles(roles);

            userRepository.save(user);
            logger.info("Created default user (username: dsa, password: Tiger)");
        }

        // Create viewer user if not exists
        if (!userRepository.existsByUsername("viewer")) {
            User viewer = new User();
            viewer.setUsername("viewer");
            viewer.setPassword(passwordEncoder.encode("viewer123"));
            viewer.setEmail("viewer@example.com");
            viewer.setEnabled(true);

            // Assign VIEWER role
            roleRepository.findByName("VIEWER").ifPresent(role -> {
                Set<Role> roles = new HashSet<>();
                roles.add(role);
                viewer.setRoles(roles);
            });

            userRepository.save(viewer);
            logger.info("Created viewer user (username: viewer, password: viewer123)");
        }

        // Create auditor user if not exists
        if (!userRepository.existsByUsername("auditor")) {
            User auditor = new User();
            auditor.setUsername("auditor");
            auditor.setPassword(passwordEncoder.encode("auditor123"));
            auditor.setEmail("auditor@example.com");
            auditor.setEnabled(true);

            // Assign AUDITOR and SECURITY_OFFICER roles
            Set<Role> roles = new HashSet<>();
            roleRepository.findByName("AUDITOR").ifPresent(roles::add);
            roleRepository.findByName("SECURITY_OFFICER").ifPresent(roles::add);
            auditor.setRoles(roles);

            userRepository.save(auditor);
            logger.info("Created auditor user (username: auditor, password: auditor123)");
        }

        logger.info("Total users in system: {}", userRepository.count());
    }
}
