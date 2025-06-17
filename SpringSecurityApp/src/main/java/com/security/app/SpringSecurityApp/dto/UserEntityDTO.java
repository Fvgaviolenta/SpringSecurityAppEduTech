package com.security.app.SpringSecurityApp.dto;
import lombok.Data;
import java.util.Set;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "DTO que representa un usuario del sistema.")
@Data
public class UserEntityDTO {

    @Schema(description = "nombre de usuario único para el acceso al sistema.", example = "alfonso.admin")
    private String username;


    @Schema(description = "Contraseña del usuario, almacenada de forma segura.", example = "password123")
    private String password;

    @Schema(description = "Indica si la cuenta del usuario está habilitada.", example = "true")
    private boolean enabled;

    @Schema(description = "Indica si la cuenta del usuario ha expirado.", example = "false")
    private boolean isAccountNoExpired;

    @Schema(description = "Indica si la cuenta del usuario está bloqueada.", example = "false")
    private boolean isAccountNoLocked;

    @Schema(description = "Indica si las credenciales del usuario han expirado.", example = "false")
    private boolean credentialNoExpired;

    @Schema(description = "Lista de roles asignados al usuario.")
    private Set<String> roles; 

}
