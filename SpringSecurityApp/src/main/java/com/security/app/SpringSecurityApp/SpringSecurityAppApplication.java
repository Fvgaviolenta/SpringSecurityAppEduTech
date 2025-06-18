package com.security.app.SpringSecurityApp;

import java.util.List;
import java.util.Set;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import com.security.app.SpringSecurityApp.persistance.entities.PermissionEntity;
import com.security.app.SpringSecurityApp.persistance.entities.RoleEntity;
import com.security.app.SpringSecurityApp.persistance.entities.RoleEnum;
import com.security.app.SpringSecurityApp.persistance.entities.UserEntity;
import com.security.app.SpringSecurityApp.persistance.repository.PermissionRepository;
import com.security.app.SpringSecurityApp.persistance.repository.RoleRepository;
import com.security.app.SpringSecurityApp.persistance.repository.UserRepository;

@SpringBootApplication
public class SpringSecurityAppApplication {

	public static void main(String[] args) {
		SpringApplication.run(SpringSecurityAppApplication.class, args);
	}	

	@Bean
CommandLineRunner init(UserRepository userRepository,
                       RoleRepository roleRepository,
                       PermissionRepository permissionRepository) {
    return args -> {
        // Crear permisos solo si no existen
        PermissionEntity create = permissionRepository.findByName("CREATE")
                .orElseGet(() -> permissionRepository.save(PermissionEntity.builder().name("CREATE").build()));

        PermissionEntity read = permissionRepository.findByName("READ")
                .orElseGet(() -> permissionRepository.save(PermissionEntity.builder().name("READ").build()));

        PermissionEntity update = permissionRepository.findByName("UPDATE")
                .orElseGet(() -> permissionRepository.save(PermissionEntity.builder().name("UPDATE").build()));

        PermissionEntity delete = permissionRepository.findByName("DELETE")
                .orElseGet(() -> permissionRepository.save(PermissionEntity.builder().name("DELETE").build()));

        // Crear roles si no existen
        RoleEntity adminRole = roleRepository.findByRoleEnum(RoleEnum.ADMIN)
                .orElseGet(() -> roleRepository.save(RoleEntity.builder()
                        .roleEnum(RoleEnum.ADMIN)
                        .permissionList(Set.of(create, read, update, delete))
                        .build()));

        RoleEntity profesorRole = roleRepository.findByRoleEnum(RoleEnum.PROFESOR)
                .orElseGet(() -> roleRepository.save(RoleEntity.builder()
                        .roleEnum(RoleEnum.PROFESOR)
                        .permissionList(Set.of(create, read, update))
                        .build()));

        RoleEntity estudianteRole = roleRepository.findByRoleEnum(RoleEnum.ESTUDIANTE)
                .orElseGet(() -> roleRepository.save(RoleEntity.builder()
                        .roleEnum(RoleEnum.ESTUDIANTE)
                        .permissionList(Set.of(read))
                        .build()));

        RoleEntity soporteRole = roleRepository.findByRoleEnum(RoleEnum.SOPORTE)
                .orElseGet(() -> roleRepository.save(RoleEntity.builder()
                        .roleEnum(RoleEnum.SOPORTE)
                        .permissionList(Set.of(create, read, update, delete))
                        .build()));

        // Crear usuarios si no existen
        if (!userRepository.existsByUsername("alfonso.admin")) {
            userRepository.save(UserEntity.builder()
                    .username("alfonso.admin")
                    .password("1234")
                    .enabled(true)
                    .accountNoExpired(true)
                    .accountNoLocked(true)
                    .credentialNoExpired(true)
                    .roles(Set.of(adminRole))
                    .build());
        }

        if (!userRepository.existsByUsername("aron.soporte")) {
            userRepository.save(UserEntity.builder()
                    .username("aron.soporte")
                    .password("1234")
                    .enabled(true)
                    .accountNoExpired(true)
                    .accountNoLocked(true)
                    .credentialNoExpired(true)
                    .roles(Set.of(soporteRole))
                    .build());
        }

        if (!userRepository.existsByUsername("hernan.profesor")) {
            userRepository.save(UserEntity.builder()
                    .username("hernan.profesor")
                    .password("1234")
                    .enabled(true)
                    .accountNoExpired(true)
                    .accountNoLocked(true)
                    .credentialNoExpired(true)
                    .roles(Set.of(profesorRole))
                    .build());
        }

        if (!userRepository.existsByUsername("fabian.estudiante")) {
            userRepository.save(UserEntity.builder()
                    .username("fabian.estudiante")
                    .password("1234")
                    .enabled(true)
                    .accountNoExpired(true)
                    .accountNoLocked(true)
                    .credentialNoExpired(true)
                    .roles(Set.of(estudianteRole))
                    .build());
        }
    };
}


}
