package com.citasmart.userservice.repository;

import com.citasmart.userservice.model.Role;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Repository interface for Role entity
 * 
 * @author CitaSmart Team
 * @version 1.0
 */
@Repository
public interface RoleRepository extends JpaRepository<Role, Long> {

    /**
     * Find role by name
     */
    Optional<Role> findByName(String name);

    /**
     * Find all active roles
     */
    List<Role> findByIsActiveTrue();

    /**
     * Check if role name exists
     */
    boolean existsByName(String name);

    /**
     * Find roles by user ID
     */
    @Query("SELECT r FROM Role r JOIN r.users u WHERE u.id = :userId")
    List<Role> findRolesByUserId(@Param("userId") Long userId);

    /**
     * Find roles with specific permission
     */
    @Query("SELECT r FROM Role r JOIN r.permissions p WHERE p.name = :permissionName")
    List<Role> findRolesByPermissionName(@Param("permissionName") String permissionName);

    /**
     * Count active roles
     */
    long countByIsActiveTrue();
}
