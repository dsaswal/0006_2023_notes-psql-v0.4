package dsa.personal.notespsqlv04.repository;

import dsa.personal.notespsqlv04.entity.Permission;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.Set;

@Repository
public interface PermissionRepository extends JpaRepository<Permission, Long> {

    /**
     * Find permission by name
     */
    Optional<Permission> findByName(String name);

    /**
     * Find multiple permissions by names
     */
    Set<Permission> findByNameIn(Set<String> names);

    /**
     * Find all permissions for a specific resource
     */
    List<Permission> findByResource(String resource);

    /**
     * Find permissions by resource and action
     */
    Optional<Permission> findByResourceAndAction(String resource, String action);

    /**
     * Check if permission exists by name
     */
    boolean existsByName(String name);

    /**
     * Find all distinct resources
     */
    @Query("SELECT DISTINCT p.resource FROM Permission p ORDER BY p.resource")
    List<String> findAllDistinctResources();

    /**
     * Find all distinct actions
     */
    @Query("SELECT DISTINCT p.action FROM Permission p ORDER BY p.action")
    List<String> findAllDistinctActions();
}
