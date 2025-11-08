package dsa.personal.notespsqlv04.repository;

import dsa.personal.notespsqlv04.entity.Role;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.Set;

@Repository
public interface RoleRepository extends JpaRepository<Role, Long> {

    /**
     * Find role by name
     */
    Optional<Role> findByName(String name);

    /**
     * Find multiple roles by names
     */
    Set<Role> findByNameIn(Set<String> names);

    /**
     * Find all non-system roles
     */
    @Query("SELECT r FROM Role r WHERE r.isSystem = false OR r.isSystem IS NULL")
    Set<Role> findAllNonSystemRoles();

    /**
     * Find all system roles
     */
    @Query("SELECT r FROM Role r WHERE r.isSystem = true")
    Set<Role> findAllSystemRoles();

    /**
     * Check if role name exists
     */
    boolean existsByName(String name);
}
