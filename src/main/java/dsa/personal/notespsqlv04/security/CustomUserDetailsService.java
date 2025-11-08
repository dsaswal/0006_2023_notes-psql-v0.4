package dsa.personal.notespsqlv04.security;

import dsa.personal.notespsqlv04.entity.Permission;
import dsa.personal.notespsqlv04.entity.Role;
import dsa.personal.notespsqlv04.entity.User;
import dsa.personal.notespsqlv04.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Timestamp;
import java.util.HashSet;
import java.util.Set;

/**
 * Custom UserDetailsService for database-backed authentication.
 * Loads user information and authorities from the database.
 */
@Service
public class CustomUserDetailsService implements UserDetailsService {

    private static final Logger logger = LoggerFactory.getLogger(CustomUserDetailsService.class);

    @Autowired
    private UserRepository userRepository;

    @Override
    @Transactional(readOnly = true)
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        logger.debug("Loading user by username: {}", username);

        User user = userRepository.findByUsernameWithRoles(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found: " + username));

        // Build authorities from roles and permissions
        Set<GrantedAuthority> authorities = buildAuthorities(user);

        logger.debug("User {} loaded with {} authorities", username, authorities.size());

        // Update last login timestamp (in a separate transaction to avoid locking)
        updateLastLogin(user.getId());

        return org.springframework.security.core.userdetails.User.builder()
                .username(user.getUsername())
                .password(user.getPassword())
                .disabled(!user.getEnabled())
                .accountExpired(!user.getAccountNonExpired())
                .accountLocked(!user.getAccountNonLocked())
                .credentialsExpired(!user.getCredentialsNonExpired())
                .authorities(authorities)
                .build();
    }

    /**
     * Build granted authorities from user roles and permissions
     */
    private Set<GrantedAuthority> buildAuthorities(User user) {
        Set<GrantedAuthority> authorities = new HashSet<>();

        // Add role-based authorities (ROLE_ prefix for Spring Security)
        for (Role role : user.getRoles()) {
            authorities.add(new SimpleGrantedAuthority("ROLE_" + role.getName()));

            // Add all permissions from the role (including inherited)
            for (Permission permission : role.getAllPermissions()) {
                authorities.add(new SimpleGrantedAuthority(permission.getName()));
            }
        }

        return authorities;
    }

    /**
     * Update last login timestamp asynchronously
     */
    @Transactional
    public void updateLastLogin(Long userId) {
        try {
            userRepository.findById(userId).ifPresent(user -> {
                user.setLastLogin(new Timestamp(System.currentTimeMillis()));
                userRepository.save(user);
            });
        } catch (Exception e) {
            logger.error("Failed to update last login for user ID: " + userId, e);
        }
    }
}
