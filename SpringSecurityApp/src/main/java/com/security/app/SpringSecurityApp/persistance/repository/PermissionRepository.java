package com.security.app.SpringSecurityApp.persistance.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.security.app.SpringSecurityApp.persistance.entities.PermissionEntity;

public interface PermissionRepository extends JpaRepository<PermissionEntity, Long> {

    Optional<PermissionEntity> findByName(String name);

}
