package dsa.personal.notespsqlv04.repository;

import dsa.personal.notespsqlv04.entity.PermissionAudit;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.sql.Timestamp;
import java.util.List;

@Repository
public interface PermissionAuditRepository extends JpaRepository<PermissionAudit, Long> {

    /**
     * Find audit logs by user ID
     */
    List<PermissionAudit> findByUserIdOrderByTimestampDesc(Long userId);

    /**
     * Find audit logs by username
     */
    List<PermissionAudit> findByUsernameOrderByTimestampDesc(String username);

    /**
     * Find audit logs for a specific resource and action
     */
    List<PermissionAudit> findByResourceAndActionOrderByTimestampDesc(String resource, String action);

    /**
     * Find denied permission attempts
     */
    List<PermissionAudit> findByGrantedFalseOrderByTimestampDesc();

    /**
     * Find audit logs within a time range
     */
    @Query("SELECT pa FROM PermissionAudit pa WHERE pa.timestamp BETWEEN :startTime AND :endTime ORDER BY pa.timestamp DESC")
    List<PermissionAudit> findByTimestampBetween(@Param("startTime") Timestamp startTime,
                                                  @Param("endTime") Timestamp endTime);

    /**
     * Find recent audit logs for a user
     */
    @Query("SELECT pa FROM PermissionAudit pa WHERE pa.userId = :userId AND pa.timestamp >= :since ORDER BY pa.timestamp DESC")
    List<PermissionAudit> findRecentByUserId(@Param("userId") Long userId,
                                             @Param("since") Timestamp since);

    /**
     * Count failed permission checks by user
     */
    @Query("SELECT COUNT(pa) FROM PermissionAudit pa WHERE pa.userId = :userId AND pa.granted = false AND pa.timestamp >= :since")
    long countFailedChecksByUserSince(@Param("userId") Long userId,
                                      @Param("since") Timestamp since);
}
