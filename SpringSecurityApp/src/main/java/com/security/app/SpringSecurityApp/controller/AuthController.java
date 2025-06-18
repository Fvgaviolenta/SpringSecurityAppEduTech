package com.security.app.SpringSecurityApp.controller;

import java.lang.annotation.Repeatable;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
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
import org.springframework.web.server.ResponseStatusException;

import com.security.app.SpringSecurityApp.dto.UserEntityDTO;
import com.security.app.SpringSecurityApp.persistance.entities.RoleEntity;
import com.security.app.SpringSecurityApp.persistance.entities.RoleEnum;
import com.security.app.SpringSecurityApp.persistance.entities.UserEntity;
import com.security.app.SpringSecurityApp.persistance.repository.RoleRepository;
import com.security.app.SpringSecurityApp.persistance.repository.UserRepository;
import com.security.app.SpringSecurityApp.service.UserDetailServiceImpl;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@RequestMapping("api/v1/auth")
@Tag(name = "AuthController", description = "Controlador para gestionar la autenticación, autorización y gestion de usuarios")
// Este controlador está configurado para denegar el acceso a todos los usuarios por defecto
//@PreAuthorize("denyAll()")
public class AuthController {

    @Autowired
    private RoleRepository roleRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private UserDetailServiceImpl userDetailServ;

    // Endppoints para verificar la seguridad para todo tipo de usuarios

    @Operation(summary = "Endpoint de prueba para verificar la seguridad")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Respuesta exitosa"),
            @ApiResponse(responseCode = "403", description = "Acceso denegado")
    })
    @GetMapping("/get")
    @PreAuthorize("hasAuthority('READ')")
    public ResponseEntity<?> helloGet(){
        return ResponseEntity.ok("Hello world - GET");
    }

    @Operation(summary = "Endpoint de prueba para verificar la seguridad con POST")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Respuesta exitosa"),
            @ApiResponse(responseCode = "403", description = "Acceso denegado")
    })
    @PostMapping("/post")
    @PreAuthorize("hasAnyRole('ADMIN','PROFESOR', 'SOPORTE')")
    public ResponseEntity<?> helloPost(){
        return ResponseEntity.ok("Hello world - POST");
    }

    @Operation(summary = "Endpoint de prueba para verificar la seguridad con PUT")
    @ApiResponses({ 
            @ApiResponse(responseCode = "200", description = "Respuesta exitosa"),
            @ApiResponse(responseCode = "403", description = "Acceso denegado")
    })
    @PutMapping("/put")
    @PreAuthorize("hasAnyRole('ADMIN','PROFESOR', 'SOPORTE')")
    public ResponseEntity<?> helloPut(){
        return ResponseEntity.ok("Hello world - PUT");
    }
    

    // Endpoints de gestion de usuarios, solo accesibles para ADMIN y SOPORTE

    @Operation(summary = "Endpoint para listar usuario por ID con HATEOAS")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Usuario encontrado"),
            @ApiResponse(responseCode = "403", description = "Acceso denegado"),
            @ApiResponse(responseCode = "404", description = "Usuario no encontrado")
    })
    @GetMapping("/get/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'SOPORTE')")
    public EntityModel<UserEntity> obtenerUsuario(@Parameter(description = "ID del usuario", example = "1") @PathVariable Long id) {
        UserEntity user = userDetailServ.findById(id);
        if (user == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Usuario no encontrado");
        }
        EntityModel<UserEntity> userModel = EntityModel.of(user);
        boolean hasAdmin = user.getRoles().stream().anyMatch(role -> role.getRoleEnum().name().equals("ADMIN"));
        boolean hasSoporte = user.getRoles().stream().anyMatch(role -> role.getRoleEnum().name().equals("SOPORTE"));
        if (hasAdmin || hasSoporte) {
            userModel.add(linkTo(methodOn(AuthController.class).obtenerUsuario(id)).withSelfRel());
            userModel.add(linkTo(methodOn(AuthController.class).listarUsuariosHabilitadosV2()).withRel("listar_usuarios_habilitados"));
            userModel.add(linkTo(methodOn(AuthController.class).listarUsuariosDeshabilitados()).withRel("listar_usuarios_deshabilitados"));
            userModel.add(linkTo(methodOn(AuthController.class).crearUsuario(UserEntityDTO.builder().build())).withRel("crear_usuario"));
            userModel.add(linkTo(methodOn(AuthController.class).DeleteUser(id)).withRel("eliminar_usuario"));
            userModel.add(linkTo(methodOn(AuthController.class).habilitarUsuario(id)).withRel("habilitar_usuario"));
        } else{
            userModel.add(linkTo(methodOn(AuthController.class).helloGet()).withRel("hello_get"));
            userModel.add(linkTo(methodOn(AuthController.class).helloPost()).withRel("hello_post"));
            userModel.add(linkTo(methodOn(AuthController.class).helloPut()).withRel("hello_put"));
        }
        return userModel;
    }

    // Este metodo esta comentado debido a que es la primera version del endpoint para obtener un usuario por ID sin usar HATEOAS
    // @Operation(summary = "Endpoint para obtener un usuario por ID")
    // @ApiResponses({
    //         @ApiResponse(responseCode = "200", description = "Usuario encontrado"),
    //         @ApiResponse(responseCode = "403", description = "Acceso denegado"),
    //         @ApiResponse(responseCode = "404", description = "Usuario no encontrado")
    // })
    // @GetMapping ("/get/{id}")
    // @PreAuthorize("hasAnyRole('ADMIN', 'SOPORTE')")
    // public ResponseEntity<UserEntity> getUserById(@Parameter(description = "ID del usuario", example = "1") @PathVariable Long id){
    //     try {
    //         UserEntity user = userDetailServ.findById(id);
    //         return ResponseEntity.ok(user);
    //     } catch (Exception e) {
    //         return ResponseEntity.notFound().build();
    //     }
    // }
    

    @Operation(summary = "Endpoint para listar usuarios habilitados con HATEOAS")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Lista de usuarios habilitados"),
            @ApiResponse(responseCode = "204", description = "No hay usuarios habilitados"),
            @ApiResponse(responseCode = "403", description = "Acceso denegado")
    })

    @GetMapping("/get/listar_usuarios_habilitados/v2")
    @PreAuthorize("hasAnyRole('ADMIN', 'SOPORTE')")
    public CollectionModel<EntityModel<UserEntity>> listarUsuariosHabilitadosV2(){
        List<UserEntity> consulta = userDetailServ.findAllByEnabledTrue();
        if (consulta.isEmpty()) {
            return CollectionModel.empty();
        }
        List<EntityModel<UserEntity>> userModels = consulta.stream()
                .map(user -> {
                    EntityModel<UserEntity> userModel = EntityModel.of(user);
                    boolean hasAdmin = user.getRoles().stream().anyMatch(role -> role.getRoleEnum().name().equals("ADMIN"));
                    boolean hasSoporte = user.getRoles().stream().anyMatch(role -> role.getRoleEnum().name().equals("SOPORTE"));
                    if (hasAdmin || hasSoporte) {
                        userModel.add(linkTo(methodOn(AuthController.class).obtenerUsuario(user.getId())).withSelfRel());
                        userModel.add(linkTo(methodOn(AuthController.class).listarUsuariosHabilitadosV2()).withRel("listar_usuarios_habilitados"));
                        userModel.add(linkTo(methodOn(AuthController.class).listarUsuariosDeshabilitados()).withRel("listar_usuarios_deshabilitados"));
                        userModel.add(linkTo(methodOn(AuthController.class).crearUsuario(UserEntityDTO.builder().build())).withRel("crear_usuario"));
                        userModel.add(linkTo(methodOn(AuthController.class).DeleteUser(user.getId())).withRel("eliminar_usuario"));
                        userModel.add(linkTo(methodOn(AuthController.class).habilitarUsuario(user.getId())).withRel("habilitar_usuario"));
                    } else {
                        userModel.add(linkTo(methodOn(AuthController.class).helloGet()).withRel("hello_get"));
                        userModel.add(linkTo(methodOn(AuthController.class).helloPost()).withRel("hello_post"));
                        userModel.add(linkTo(methodOn(AuthController.class).helloPut()).withRel("hello_put"));
                    }
                    return userModel;
                }).toList();
        CollectionModel<EntityModel<UserEntity>> collectionModel = CollectionModel.of(userModels);
        collectionModel.add(linkTo(methodOn(AuthController.class).listarUsuariosHabilitadosV2()).withSelfRel());
        return collectionModel;
    }

    // Este metodo esta comentado debido a que es la primera version del endpoint para listar usuarios habilitados sin usar HATEOAS
    // @Operation(summary = "Endpoint para listar todos los usuarios habilitados")
    // @ApiResponses({
    //         @ApiResponse(responseCode = "200", description = "Lista de usuarios habilitados"),
    //         @ApiResponse(responseCode = "204", description = "No hay usuarios habilitados"),
    //         @ApiResponse(responseCode = "403", description = "Acceso denegado")
    // })
    // @GetMapping("/get/listar_usuarios_habilitados")
    // @PreAuthorize("hasAnyRole('ADMIN', 'SOPORTE')")
    // public ResponseEntity<List<UserEntity>> listarUsuarios(){
    //     List<UserEntity> consulta = userDetailServ.findAllByEnabledTrue();
    //     if (consulta.isEmpty()) {
    //         return ResponseEntity.noContent().build();
    //     }
    //     return ResponseEntity.ok(consulta);
    // }

    @Operation(summary = "Endpoint para listar todos los usuarios deshabilitados")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Lista de usuarios deshabilitados"),
            @ApiResponse(responseCode = "204", description = "No hay usuarios deshabilitados"),
            @ApiResponse(responseCode = "403", description = "Acceso denegado")
    })
    @GetMapping("/get/listar_usuarios_deshabilitados")
    @PreAuthorize("hasAnyRole('ADMIN', 'SOPORTE')")
    public ResponseEntity<List<UserEntity>> listarUsuariosDeshabilitados(){
        List<UserEntity> consulta = userDetailServ.findAllByEnabledFalse();
        if (consulta.isEmpty()) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok(consulta);
    }


    @Operation(summary = "Endpoint para crear un nuevo usuario")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Usuario creado exitosamente"),
            @ApiResponse(responseCode = "400", description = "Solicitud incorrecta"),
            @ApiResponse(responseCode = "403", description = "Acceso denegado")
    })
    @PostMapping("/post/crear_usuario")
    @PreAuthorize("hasAnyRole('ADMIN', 'SOPORTE')")
    
    public ResponseEntity<UserEntity> crearUsuario(@io.swagger.v3.oas.annotations.parameters.RequestBody(description = "Datos del nuevo usuario", required = true, 
            content = @Content(schema = @Schema(implementation = UserEntityDTO.class)))
        @RequestBody UserEntityDTO newUserDTO){

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

    

    @Operation(summary = "Endpoint para eliminar un usuario por ID")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Usuario eliminado correctamente"),
            @ApiResponse(responseCode = "404", description = "Usuario no encontrado"),
            @ApiResponse(responseCode = "403", description = "Acceso denegado")
    })
    @DeleteMapping("/delete/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'SOPORTE')")
    public ResponseEntity<?> DeleteUser(@Parameter(description = "ID del usuario a deshabilitar", example = "1") @PathVariable Long id){
        userDetailServ.deshabilitarUsuarioById(id);
        return ResponseEntity.ok().body("Usuario con id: " + id + " deshabilitado correctamente.");
    }

    @Operation(summary = "Endpoint para habilitar un usuario por ID")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Usuario habilitado correctamente"),
            @ApiResponse(responseCode = "404", description = "Usuario no encontrado"),
            @ApiResponse(responseCode = "403", description = "Acceso denegado")
    })
    @PutMapping("/put/habilitar/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'SOPORTE')")
    public ResponseEntity<?> habilitarUsuario(@Parameter(description = "ID del usuario a deshabilitar", example = "1") @PathVariable Long id){
        userDetailServ.habilitarUsuarioById(id);
        return ResponseEntity.ok().body("Usuario con id: " + id + " habilitado correctamente.");
    }

    @Operation(summary = "Endpoint para actualizar un usuario por ID")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Usuario actualizado correctamente",
                content = @Content(schema = @Schema(implementation = UserEntityDTO.class))),
            @ApiResponse(responseCode = "404", description = "Usuario no encontrado"),
            @ApiResponse(responseCode = "403", description = "Acceso denegado")
    })
    @PutMapping("/put/actualizar/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'SOPORTE')")
    public ResponseEntity<?> actualizarUsuario(@Parameter(description = "ID del usuario a actualizar", example = "1") @PathVariable Long id, 
    @io.swagger.v3.oas.annotations.parameters.RequestBody(description = "Datos actualizados del usuario", required = true, content = @Content(schema = @Schema(implementation = UserEntityDTO.class))) @RequestBody UserEntityDTO userDTO){
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
