package com.security.app.SpringSecurityApp.controller;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auth")
@PreAuthorize("denyAll()")
public class TestAuthController {

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

    @PutMapping("/put")
    @PreAuthorize("hasAnyRole('ADMIN','PROFESOR')")
    public String helloPut(){
        return "hello world - PUT";
    }

    @DeleteMapping("/delete")
    @PreAuthorize("hasAnyRole('ADMIN','PROFESOR')")
    public String helloDelete(){
        return "hello world - DELETE";
    }
}
