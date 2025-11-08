package dsa.personal.notespsqlv04.security;

import dsa.personal.notespsqlv04.entity.Permission;
import dsa.personal.notespsqlv04.service.AuditService;
import dsa.personal.notespsqlv04.service.PermissionService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.PermissionEvaluator;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

import java.io.Serializable;
import java.util.Set;

/**
 * Custom PermissionEvaluator for dynamic permission checking.
 * Used by Spring Security's @PreAuthorize and @PostAuthorize annotations.
 */
@Component
public class CustomPermissionEvaluator implements PermissionEvaluator {

    private static final Logger logger = LoggerFactory.getLogger(CustomPermissionEvaluator.class);

    @Autowired
    private PermissionService permissionService;

    @Autowired
    private AuditService auditService;

    /**
     * Check permission with domain object
     * Usage: @PreAuthorize("hasPermission(#account, 'READ')")
     */
    @Override
    public boolean hasPermission(Authentication authentication, Object targetDomainObject, Object permission) {
        if (authentication == null || permission == null) {
            return false;
        }

        String username = authentication.getName();
        String resource = extractResource(targetDomainObject);
        String action = permission.toString();

        return checkAndAuditPermission(username, resource, null, action);
    }

    /**
     * Check permission with target ID and type
     * Usage: @PreAuthorize("hasPermission(#id, 'ACCOUNT', 'READ')")
     */
    @Override
    public boolean hasPermission(Authentication authentication, Serializable targetId,
                                String targetType, Object permission) {
        if (authentication == null || permission == null) {
            return false;
        }

        String username = authentication.getName();
        String resource = targetType.toUpperCase();
        String action = permission.toString();
        String resourceId = targetId != null ? targetId.toString() : null;

        return checkAndAuditPermission(username, resource, resourceId, action);
    }

    /**
     * Extract resource name from domain object
     */
    private String extractResource(Object targetDomainObject) {
        if (targetDomainObject == null) {
            return "UNKNOWN";
        }

        // Use simple class name as resource
        String className = targetDomainObject.getClass().getSimpleName();
        return className.toUpperCase();
    }

    /**
     * Check permission and audit the result
     */
    private boolean checkAndAuditPermission(String username, String resource,
                                           String resourceId, String action) {
        logger.debug("Checking permission for user: {} on resource: {} action: {}",
                username, resource, action);

        // Get all user permissions
        Set<Permission> userPermissions = permissionService.getUserPermissions(username);

        // Check if user has matching permission
        boolean granted = userPermissions.stream()
                .anyMatch(p -> p.matches(resource, action));

        // Audit the permission check
        if (resourceId != null) {
            auditService.logPermissionCheck(username, resource, resourceId, action, granted, null);
        } else {
            auditService.logPermissionCheck(username, resource, action, granted);
        }

        if (!granted) {
            logger.warn("Permission DENIED for user: {} on resource: {} action: {}",
                    username, resource, action);
        } else {
            logger.debug("Permission GRANTED for user: {} on resource: {} action: {}",
                    username, resource, action);
        }

        return granted;
    }
}
