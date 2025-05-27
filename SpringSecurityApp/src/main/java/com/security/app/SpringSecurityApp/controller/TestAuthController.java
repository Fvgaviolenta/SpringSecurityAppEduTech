package com.security.app.SpringSecurityApp.controller;

import java.util.HashSet;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.security.app.SpringSecurityApp.dto.UserEntityDTO;
import com.security.app.SpringSecurityApp.persistance.entities.RoleEntity;
import com.security.app.SpringSecurityApp.persistance.entities.RoleEnum;
import com.security.app.SpringSecurityApp.persistance.entities.UserEntity;
import com.security.app.SpringSecurityApp.persistance.repository.RoleRepository;
import com.security.app.SpringSecurityApp.persistance.repository.UserRepository;
import com.security.app.SpringSecurityApp.service.UserDetailServiceImpl;

@RestController
@RequestMapping("/auth")
@PreAuthorize("denyAll()")
public class TestAuthController {

    @Autowired
    private RoleRepository roleRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private UserDetailServiceImpl userDetailServ;


    @GetMapping("/get")
    @PreAuthorize("hasAuthority('READ')")
    public String helloGet(){
        return "Hello world - GET";
    }

    @PostMapping("/post")
    @PreAuthorize("hasAnyRole('ADMIN','PROFESOR')")
    public String helloPost(){
        return "Hello world - POST";
    }

    @PostMapping("/post/creacion_usuario")
    @PreAuthorize("hasAnyRole('ADMIN')")
    public ResponseEntity<UserEntity> crearUsuario(@RequestBody UserEntityDTO newUserDTO){

        Set<RoleEntity> rolesAsignados = new HashSet<>();
        for (String nombreRol : newUserDTO.getRoles()) {
            RoleEnum rolEnum = RoleEnum.valueOf(nombreRol);
            RoleEntity rol = roleRepository.findByRoleEnum(rolEnum)
                    .orElseThrow(()-> new RuntimeException("Rol no encontrado: " + nombreRol));
            rolesAsignados.add(rol);
        }

        UserEntity nuevoUsuario = UserEntity.builder()
                .username(newUserDTO.getUsername())
                .password(newUserDTO.getPassword())
                .isEnable(newUserDTO.isEnable())
                .accountNoExpired(newUserDTO.isAccountNoExpired())
                .accountNoLocked(newUserDTO.isAccountNoLocked())
                .credentialNoExpired(newUserDTO.isAccountNoExpired())
                .roles(rolesAsignados)
                .build();
        
        UserEntity usuarioGuardado = userRepository.save(nuevoUsuario);
        return ResponseEntity.status(HttpStatus.CREATED).body(usuarioGuardado);
    }

    @PutMapping("/put")
    @PreAuthorize("hasAnyRole('ADMIN','PROFESOR')")
    public String helloPut(){
        return "hello world - PUT";
    }

    @DeleteMapping("/delete/{id}")
    @PreAuthorize("hasAnyRole('ADMIN')")
    public String DeleteUser(@PathVariable Long id){
        userDetailServ.eliminarUsuarioById(id);
        return "Usuario con id: " + id + " eliminado.";
    }
}
