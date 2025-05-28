package com.security.app.SpringSecurityApp.controller;

import java.util.HashSet;
import java.util.List;
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
@RequestMapping("api/v1/auth")
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

    @GetMapping("/get/listar_usuarios_habilitados")
    @PreAuthorize("hasAnyRole('ADMIN', 'SOPORTE')")
    public ResponseEntity<List<UserEntity>> listarUsuarios(){
        List<UserEntity> consulta = userDetailServ.findAllByEnabledTrue();
        if (consulta.isEmpty()) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok(consulta);
    }

    @GetMapping("/get/listar_usuarios_deshabilitados")
    @PreAuthorize("hasAnyRole('ADMIN', 'SOPORTE')")
    public ResponseEntity<List<UserEntity>> listarUsuariosDeshabilitados(){
        List<UserEntity> consulta = userDetailServ.findAllByEnabledFalse();
        if (consulta.isEmpty()) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok(consulta);
    }

    @PostMapping("/post")
    @PreAuthorize("hasAnyRole('ADMIN','PROFESOR', 'SOPORTE')")
    public String helloPost(){
        return "Hello world - POST";
    }

    @PostMapping("/post/crear_usuario")
    @PreAuthorize("hasAnyRole('ADMIN', 'SOPORTE')")
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
                .enabled(newUserDTO.isEnabled())
                .accountNoExpired(newUserDTO.isAccountNoExpired())
                .accountNoLocked(newUserDTO.isAccountNoLocked())
                .credentialNoExpired(newUserDTO.isAccountNoExpired())
                .roles(rolesAsignados)
                .build();
        
        UserEntity usuarioGuardado = userRepository.save(nuevoUsuario);
        return ResponseEntity.status(HttpStatus.CREATED).body(usuarioGuardado);
    }

    @PutMapping("/put")
    @PreAuthorize("hasAnyRole('ADMIN','PROFESOR', 'SOPORTE')")
    public String helloPut(){
        return "hello world - PUT";
    }

    @DeleteMapping("/delete/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'SOPORTE')")
    public String DeleteUser(@PathVariable Long id){
        userDetailServ.deshabilitarUsuarioById(id);
        return "Usuario con id: " + id + " deshabilitado correctamente.";
    }

    @PutMapping("/put/habilitar/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'SOPORTE')")
    public String habilitarUsuario(@PathVariable Long id){
        userDetailServ.habilitarUsuarioById(id);
        return "Usuario con id: " + id + " habilitado correctamente.";
    }

    @PutMapping("/put/actualizar/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'SOPORTE')")
    public ResponseEntity actualizarUsuario(@PathVariable Long id, @RequestBody UserEntityDTO userDTO){
        try {
            Set<RoleEntity> rolesAsignados = new HashSet<>();
            for (String nombreRol : userDTO.getRoles()) {
                RoleEnum rolEnum = RoleEnum.valueOf(nombreRol);
                RoleEntity rol = roleRepository.findByRoleEnum(rolEnum)
                        .orElseThrow(()-> new RuntimeException("Rol no encontrado: " + nombreRol));
                rolesAsignados.add(rol);
            }
            UserEntity userEntity = userDetailServ.findById(id);
            userEntity.setUsername(userDTO.getUsername());
            userEntity.setPassword(userDTO.getPassword());
            userEntity.setEnabled(userDTO.isEnabled());
            userEntity.setAccountNoExpired(userDTO.isAccountNoExpired());
            userEntity.setAccountNoLocked(userDTO.isAccountNoLocked());
            userEntity.setCredentialNoExpired(userDTO.isCredentialNoExpired());

            userDetailServ.save(userEntity);
            return ResponseEntity.ok(userEntity);
        } catch (Exception e) {
            return ResponseEntity.notFound().build();
        }
    }
}
