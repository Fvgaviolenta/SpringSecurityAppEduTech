package com.security.app.SpringSecurityApp.dto;
import lombok.Data;
import java.util.Set;

@Data
public class UserEntityDTO {

    private String username;
    private String password;
    private boolean enabled;
    private boolean isAccountNoExpired;
    private boolean isAccountNoLocked;
    private boolean credentialNoExpired;
    private Set<String> roles; 

}
