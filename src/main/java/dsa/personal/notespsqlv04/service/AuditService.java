package dsa.personal.notespsqlv04.service;

import dsa.personal.notespsqlv04.entity.PermissionAudit;
import dsa.personal.notespsqlv04.repository.PermissionAuditRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Timestamp;
import java.util.List;

/**
 * Service for auditing permission checks.
 * Critical for compliance in financial services applications.
 */
@Service
public class AuditService {

    private static final Logger logger = LoggerFactory.getLogger(AuditService.class);

    @Autowired
    private PermissionAuditRepository auditRepository;

    /**
     * Log a permission check asynchronously
     */
    @Async
    @Transactional
    public void logPermissionCheck(String username, String resource, String action, boolean granted) {
        try {
            PermissionAudit audit = new PermissionAudit();
            audit.setUsername(username);
            audit.setPermission(resource + ":" + action);
            audit.setResource(resource);
            audit.setAction(action);
            audit.setGranted(granted);

            auditRepository.save(audit);

            if (!granted) {
                logger.warn("Permission DENIED for user: {} on resource: {} action: {}",
                        username, resource, action);
            } else {
                logger.debug("Permission GRANTED for user: {} on resource: {} action: {}",
                        username, resource, action);
            }
        } catch (Exception e) {
            logger.error("Failed to log permission check for user: " + username, e);
        }
    }

    /**
     * Log a permission check with additional details
     */
    @Async
    @Transactional
    public void logPermissionCheck(String username, String resource, String resourceId,
                                   String action, boolean granted, String details) {
        try {
            PermissionAudit audit = new PermissionAudit();
            audit.setUsername(username);
            audit.setPermission(resource + ":" + action);
            audit.setResource(resource);
            audit.setResourceId(resourceId);
            audit.setAction(action);
            audit.setGranted(granted);
            audit.setDetails(details);

            auditRepository.save(audit);

            if (!granted) {
                logger.warn("Permission DENIED for user: {} on resource: {}:{} action: {}",
                        username, resource, resourceId, action);
            } else {
                logger.debug("Permission GRANTED for user: {} on resource: {}:{} action: {}",
                        username, resource, resourceId, action);
            }
        } catch (Exception e) {
            logger.error("Failed to log permission check for user: " + username, e);
        }
    }

    /**
     * Get audit logs for a specific user
     */
    public List<PermissionAudit> getUserAuditLogs(String username) {
        return auditRepository.findByUsernameOrderByTimestampDesc(username);
    }

    /**
     * Get recent failed permission attempts for a user
     */
    public List<PermissionAudit> getRecentFailedAttempts(Long userId, int hours) {
        Timestamp since = new Timestamp(System.currentTimeMillis() - (hours * 3600000L));
        return auditRepository.findRecentByUserId(userId, since);
    }

    /**
     * Get all denied permission attempts
     */
    public List<PermissionAudit> getAllDeniedAttempts() {
        return auditRepository.findByGrantedFalseOrderByTimestampDesc();
    }

    /**
     * Get audit logs within a time range
     */
    public List<PermissionAudit> getAuditLogsBetween(Timestamp start, Timestamp end) {
        return auditRepository.findByTimestampBetween(start, end);
    }

    /**
     * Count failed permission checks for a user in the last N hours
     */
    public long countFailedChecks(Long userId, int hours) {
        Timestamp since = new Timestamp(System.currentTimeMillis() - (hours * 3600000L));
        return auditRepository.countFailedChecksByUserSince(userId, since);
    }
}
