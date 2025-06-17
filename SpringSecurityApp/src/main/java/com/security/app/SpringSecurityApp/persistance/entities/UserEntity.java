package com.security.app.SpringSecurityApp.persistance.entities;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.hibernate.annotations.ManyToAny;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "user")
@Schema(description = "Entidad que representa un usuario del sistema.")
public class UserEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Schema(description = "Identificador único del usuario.", example = "1")
    private Long Id;

    @Schema(description = "Nombre de usuario único para el acceso al sistema.", example = "aron.soporte")
    @Column(name = "username", unique = true)
    private String username;

    @Schema(description = "Contraseña del usuario, almacenada de forma segura.", example = "password123")
    private String password;

    @Schema(description = "Indica si la cuenta del usuario está habilitada.", example = "true")
    @Column(name = "is_enable")
    private boolean enabled;

    @Schema(description = "Indica si la cuenta del usuario ha expirado.", example = "false")
    @Column(name = "account_no_expired")
    private boolean accountNoExpired;

    @Schema(description = "Indica si la cuenta del usuario está bloqueada.", example = "false")
    @Column(name = "account_no_locked")
    private boolean accountNoLocked;

    @Schema(description = "Indica si las credenciales del usuario han expirado.", example = "false")
    @Column(name = "credential_no_expired")
    private boolean credentialNoExpired;

    @Schema(description = "Lista de roles asignados al usuario.")
    @ManyToMany(fetch = FetchType.EAGER, cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    @JoinTable(name = "user_roles", joinColumns = @JoinColumn(name = "user_id"), inverseJoinColumns = @JoinColumn(name = "role_id"))
    private Set<RoleEntity> roles = new HashSet<>();

}
