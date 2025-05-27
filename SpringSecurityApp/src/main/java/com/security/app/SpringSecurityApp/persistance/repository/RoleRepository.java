package com.security.app.SpringSecurityApp.persistance.repository;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import com.security.app.SpringSecurityApp.persistance.entities.RoleEntity;
import com.security.app.SpringSecurityApp.persistance.entities.RoleEnum;

@Repository
public interface RoleRepository extends JpaRepository<RoleEntity, Long>{

    Optional<RoleEntity> findByRoleEnum(RoleEnum roleEnum);

}
