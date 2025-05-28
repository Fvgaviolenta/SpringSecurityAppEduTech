package com.security.app.SpringSecurityApp.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.security.app.SpringSecurityApp.persistance.entities.UserEntity;
import com.security.app.SpringSecurityApp.persistance.repository.UserRepository;

@Service
public class UserDetailServiceImpl implements UserDetailsService{



    @Autowired
    private UserRepository userRepository;

    public void deshabilitarUsuarioById(Long id){
        UserEntity user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Usuario con ID: " + id + " no encontrado."));
        
        user.setEnabled(false);
        userRepository.save(user);
    }

    public void habilitarUsuarioById(Long id){
        UserEntity user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Usuario con ID: " + id + " no encontrado."));
        
        user.setEnabled(true);
        userRepository.save(user);
    }

    public List<UserEntity> findAllByEnabledTrue(){
        return userRepository.findAllByEnabledTrue();
    }

    public List<UserEntity> findAllByEnabledFalse(){
        return userRepository.findAllByEnabledFalse();
    }

    public List<UserEntity> findAll(){
        return userRepository.findAll();
    }

    public UserEntity findById(Long id){
        return userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Usuario con ID: " + id + " no encontrado"));
    }




    public UserEntity save(UserEntity newUser){
        return userRepository.save(newUser);
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {

        UserEntity userEntity = userRepository.findUserEntityByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("El usuario " + username + " no existe."));

        List<SimpleGrantedAuthority> authorityList = new ArrayList<>();

        userEntity.getRoles()
                .forEach(role -> authorityList.add(new SimpleGrantedAuthority("ROLE_".concat(role.getRoleEnum().name()))));

        userEntity.getRoles().stream()
                .flatMap(role -> role.getPermissionList().stream())
                .forEach(permission -> authorityList.add(new SimpleGrantedAuthority(permission.getName())));

        return new User(userEntity.getUsername(),
                userEntity.getPassword(),
                userEntity.isEnabled(),
                userEntity.isAccountNoExpired(),
                userEntity.isCredentialNoExpired(),
                userEntity.isAccountNoLocked(),
                authorityList);
    }
}