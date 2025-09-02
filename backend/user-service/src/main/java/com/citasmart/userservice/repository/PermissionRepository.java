package com.citasmart.userservice.repository;

import com.citasmart.userservice.model.Permission;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Repository interface for Permission entity
 * 
 * @author CitaSmart Team
 * @version 1.0
 */
@Repository
public interface PermissionRepository extends JpaRepository<Permission, Long> {

    /**
     * Find permission by name
     */
    Optional<Permission> findByName(String name);

    /**
     * Find all active permissions
     */
    List<Permission> findByIsActiveTrue();

    /**
     * Check if permission name exists
     */
    boolean existsByName(String name);

    /**
     * Find permissions by resource
     */
    List<Permission> findByResource(String resource);

    /**
     * Find permissions by action
     */
    List<Permission> findByAction(String action);

    /**
     * Find permissions by resource and action
     */
    Optional<Permission> findByResourceAndAction(String resource, String action);

    /**
     * Find permissions by role ID
     */
    @Query("SELECT p FROM Permission p JOIN p.roles r WHERE r.id = :roleId")
    List<Permission> findPermissionsByRoleId(@Param("roleId") Long roleId);

    /**
     * Find permissions by user ID (through roles)
     */
    @Query("SELECT DISTINCT p FROM Permission p JOIN p.roles r JOIN r.users u WHERE u.id = :userId")
    List<Permission> findPermissionsByUserId(@Param("userId") Long userId);

    /**
     * Count active permissions
     */
    long countByIsActiveTrue();
}
