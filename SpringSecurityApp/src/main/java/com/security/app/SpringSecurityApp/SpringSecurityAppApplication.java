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
import com.security.app.SpringSecurityApp.persistance.repository.UserRepository;

@SpringBootApplication
public class SpringSecurityAppApplication {

	public static void main(String[] args) {
		SpringApplication.run(SpringSecurityAppApplication.class, args);
	}

	@Bean
	CommandLineRunner init(UserRepository userRepository){
		return args -> {
			//CREANDO PERMISSIONS
			PermissionEntity createPermission = PermissionEntity.builder()
					.name("CREATE")
					.build();
			
			PermissionEntity readPermission = PermissionEntity.builder()
					.name("READ")
					.build();
			
			PermissionEntity updatePermission = PermissionEntity.builder()
					.name("UPDATE")
					.build();

			PermissionEntity deletePermission = PermissionEntity.builder()
					.name("DELETE")
					.build();

			//CREANDO ROLES
			RoleEntity roleAdmin = RoleEntity.builder()
					.roleEnum(RoleEnum.ADMIN)
					.permissionList(Set.of(createPermission, readPermission, updatePermission, deletePermission))
					.build();

			RoleEntity roleProfesor = RoleEntity.builder()
					.roleEnum(RoleEnum.PROFESOR)
					.permissionList(Set.of(createPermission, readPermission, updatePermission))
					.build();

			RoleEntity roleEstudiante = RoleEntity.builder()
					.roleEnum(RoleEnum.ESTUDIANTE)
					.permissionList(Set.of(readPermission))
					.build();

			RoleEntity roleSoporte = RoleEntity.builder()
					.roleEnum(RoleEnum.SOPORTE)
					.permissionList(Set.of(readPermission, createPermission, updatePermission, deletePermission))
					.build();

			//CREANDO USUARIOS
			UserEntity userAdmin = UserEntity.builder()
					.username("alfonso.admin")
					.password("1234")
					.enabled(true)
					.accountNoExpired(true)
					.accountNoLocked(true)
					.credentialNoExpired(true)
					.roles(Set.of(roleAdmin))
					.build();

			UserEntity userSoporte = UserEntity.builder()
					.username("aron.soporte")
					.password("1234")
					.enabled(true)
					.accountNoExpired(true)
					.accountNoLocked(true)
					.credentialNoExpired(true)
					.roles(Set.of(roleSoporte))
					.build();

			UserEntity userProfesor = UserEntity.builder()
					.username("hernan.profesor")
					.password("1234")
					.enabled(true)
					.accountNoExpired(true)
					.accountNoLocked(true)
					.credentialNoExpired(true)
					.roles(Set.of(roleProfesor))
					.build();
			
			UserEntity userEstudiante = UserEntity.builder()
					.username("fabian.estudiante")
					.password("1234")
					.enabled(true)
					.accountNoExpired(true)
					.accountNoLocked(true)
					.credentialNoExpired(true)
					.roles(Set.of(roleEstudiante))
					.build();	

			userRepository.saveAll(List.of(userAdmin, userSoporte, userProfesor, userEstudiante));
		};
	}

}
