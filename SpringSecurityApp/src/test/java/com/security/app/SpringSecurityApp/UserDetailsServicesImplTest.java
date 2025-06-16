package com.security.app.SpringSecurityApp;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;
import java.util.Set;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.core.userdetails.UserDetails;

import com.security.app.SpringSecurityApp.persistance.entities.PermissionEntity;
import com.security.app.SpringSecurityApp.persistance.entities.RoleEntity;
import com.security.app.SpringSecurityApp.persistance.entities.RoleEnum;
import com.security.app.SpringSecurityApp.persistance.entities.UserEntity;
import com.security.app.SpringSecurityApp.persistance.repository.UserRepository;
import com.security.app.SpringSecurityApp.service.UserDetailServiceImpl;

@SpringBootTest
public class UserDetailsServicesImplTest {
    @Autowired
    private UserDetailServiceImpl userDetailService;

    @MockBean
    private UserRepository userRepository;

    @Test
    public void testLoadUserByUsername() {
        
        String username = "testUser";
        String password = "testPassword";

        PermissionEntity permission1 = new PermissionEntity();
        permission1.setName("READ");

        PermissionEntity permission2 = new PermissionEntity();
        permission2.setName("PUT");

        PermissionEntity permission3 = new PermissionEntity();
        permission3.setName("POST");

        PermissionEntity permission4 = new PermissionEntity();
        permission4.setName("DELETE");

        RoleEntity role = new RoleEntity();
        role.setRoleEnum(RoleEnum.ADMIN);
        role.setPermissionList(Set.of(permission1, permission2, permission3, permission4));

        UserEntity user = new UserEntity();
        user.setId(1L);
        user.setUsername(username);
        user.setPassword(password);
        user.setEnabled(true);
        user.setAccountNoExpired(true);
        user.setCredentialNoExpired(true);
        user.setAccountNoLocked(true);
        user.setRoles(Set.of(role));

        Mockito.when(userRepository.findUserEntityByUsername(username))
                .thenReturn(java.util.Optional.of(user));

        UserDetails result = userDetailService.loadUserByUsername(username);

        assertEquals(username, result.getUsername());
        assertEquals(password, result.getPassword());
        assertTrue(result.getAuthorities().stream()
            .anyMatch(auth -> auth.getAuthority().equals("READ")));
    }

    @Test
    public void testFindAllByEnabledTrue() {
        PermissionEntity permission1 = new PermissionEntity();
        permission1.setName("READ");

        RoleEntity role = new RoleEntity();
        role.setRoleEnum(RoleEnum.ADMIN);
        role.setPermissionList(Set.of(permission1));

        UserEntity user1 = new UserEntity();
        user1.setId(1L);
        user1.setUsername("user1");
        user1.setPassword("password1");
        user1.setEnabled(true);
        user1.setRoles(Set.of(role));

        UserEntity user2 = new UserEntity();
        user2.setId(2L);
        user2.setUsername("user2");
        user2.setPassword("password2");
        user2.setEnabled(true);
        user2.setRoles(Set.of(role));

        Mockito.when(userRepository.findAllByEnabledTrue())
                .thenReturn(List.of(user1, user2));
    }

    @Test
    public void testFindAllByEnabledFalse() {
        PermissionEntity permission1 = new PermissionEntity();
        permission1.setName("READ");

        RoleEntity role = new RoleEntity();
        role.setRoleEnum(RoleEnum.ADMIN);
        role.setPermissionList(Set.of(permission1));

        UserEntity user1 = new UserEntity();
        user1.setId(3L);
        user1.setUsername("user3");
        user1.setPassword("password3");
        user1.setEnabled(false);
        user1.setRoles(Set.of(role));

        UserEntity user2 = new UserEntity();
        user2.setId(4L);
        user2.setUsername("user4");
        user2.setPassword("password4");
        user2.setEnabled(false);
        user2.setRoles(Set.of(role));

        Mockito.when(userRepository.findAllByEnabledFalse())
                .thenReturn(List.of(user1, user2));
    }

    @Test
    public void testFindAll() {
        PermissionEntity permission1 = new PermissionEntity();
        permission1.setName("READ");

        RoleEntity role = new RoleEntity();
        role.setRoleEnum(RoleEnum.ADMIN);
        role.setPermissionList(Set.of(permission1));

        UserEntity user1 = new UserEntity();
        user1.setId(5L);
        user1.setUsername("user5");
        user1.setPassword("password5");
        user1.setEnabled(true);
        user1.setRoles(Set.of(role));

        UserEntity user2 = new UserEntity();
        user2.setId(6L);
        user2.setUsername("user6");
        user2.setPassword("password6");
        user2.setEnabled(false);
        user2.setRoles(Set.of(role));

        Mockito.when(userRepository.findAll())
                .thenReturn(List.of(user1, user2));
    }

    @Test
    public void testFindById() {
        Long userId = 1L;

        PermissionEntity permission1 = new PermissionEntity();
        permission1.setName("READ");

        RoleEntity role = new RoleEntity();
        role.setRoleEnum(RoleEnum.ADMIN);
        role.setPermissionList(Set.of(permission1));

        UserEntity user = new UserEntity();
        user.setId(userId);
        user.setUsername("testUser");
        user.setPassword("testPassword");
        user.setEnabled(true);
        user.setRoles(Set.of(role));

        Mockito.when(userRepository.findById(userId))
                .thenReturn(java.util.Optional.of(user));
    }

    @Test
    public void testDeshabilitarUsuarioById() {
        Long userId = 1L;

        PermissionEntity permission1 = new PermissionEntity();
        permission1.setName("READ");

        RoleEntity role = new RoleEntity();
        role.setRoleEnum(RoleEnum.ADMIN);
        role.setPermissionList(Set.of(permission1));

        UserEntity user = new UserEntity();
        user.setId(userId);
        user.setUsername("testUser");
        user.setPassword("testPassword");
        user.setEnabled(true);
        user.setRoles(Set.of(role));

        Mockito.when(userRepository.findById(userId))
                .thenReturn(java.util.Optional.of(user));

        userDetailService.deshabilitarUsuarioById(userId);

        Mockito.verify(userRepository).save(Mockito.any(UserEntity.class));
    }

    @Test
    public void testHabilitarUsuarioById() {
        Long userId = 1L;

        PermissionEntity permission1 = new PermissionEntity();
        permission1.setName("READ");

        RoleEntity role = new RoleEntity();
        role.setRoleEnum(RoleEnum.ADMIN);
        role.setPermissionList(Set.of(permission1));

        UserEntity user = new UserEntity();
        user.setId(userId);
        user.setUsername("testUser");
        user.setPassword("testPassword");
        user.setEnabled(false);
        user.setRoles(Set.of(role));

        Mockito.when(userRepository.findById(userId))
                .thenReturn(java.util.Optional.of(user));

        userDetailService.habilitarUsuarioById(userId);

        Mockito.verify(userRepository).save(Mockito.any(UserEntity.class));
    }

    @Test
    public void testSaveUser() {
        PermissionEntity permission1 = new PermissionEntity();
        permission1.setName("READ");

        RoleEntity role = new RoleEntity();
        role.setRoleEnum(RoleEnum.ADMIN);
        role.setPermissionList(Set.of(permission1));

        UserEntity user = new UserEntity();
        user.setId(1L);
        user.setUsername("newUser");
        user.setPassword("newPassword");
        user.setEnabled(true);
        user.setRoles(Set.of(role));

        Mockito.when(userRepository.save(user)).thenReturn(user);

        UserEntity savedUser = userDetailService.save(user);

        Mockito.verify(userRepository).save(user);
    }



}
