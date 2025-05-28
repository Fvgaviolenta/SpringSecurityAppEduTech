package com.security.app.SpringSecurityApp.persistance.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.security.app.SpringSecurityApp.persistance.entities.UserEntity;
import java.util.List;



@Repository
public interface UserRepository extends JpaRepository<UserEntity, Long>{

    Optional<UserEntity> findUserEntityByUsername(String username);

    List<UserEntity> findAllByEnabledTrue();
    List<UserEntity> findAllByEnabledFalse();
    List<UserEntity> findAll();


}
