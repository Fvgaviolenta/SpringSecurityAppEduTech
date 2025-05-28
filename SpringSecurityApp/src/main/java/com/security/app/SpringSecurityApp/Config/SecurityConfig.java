package com.security.app.SpringSecurityApp.Config;


import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.password.NoOpPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

import com.security.app.SpringSecurityApp.service.UserDetailServiceImpl;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class SecurityConfig {


    // PRIME FILTER CHAIN CREADO, ES MEJOR Y MAS LIMPIO TRABAJAR ESTO CON NOTATIONS
    //ENVES DE UN METODO TAN COMPLEJO DE CONFIGURAR
    // @Bean
    // public SecurityFilterChain securityFilterChain(HttpSecurity httpSecurity) throws Exception{
    //         return httpSecurity
    //                 .csrf(csrf -> csrf.disable())
    //                 .httpBasic(Customizer.withDefaults())
    //                 .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
    //                 .authorizeHttpRequests(http -> {
    //                     //CONFIGURAR LOS END POINTS PUBLICOS
    //                     http.requestMatchers(HttpMethod.GET,"/auth/hello").permitAll();
    //                     //CONFIGURAR LOS ENDPOINTS PRIVADORS
    //                     http.requestMatchers(HttpMethod.GET,"/auth/hello-secured").hasAuthority("READ");
    //                     //CONFIGURAR LOS OTROS ENDPOINTS NO ESPECIFICADOS
    //                     http.anyRequest().authenticated();
    //                 })
    //                 .build(); 
    // }

    //NUEVO FILTER CHAIN MAS SIMPLE, VALIDA LA ENTRADA A CADA END POINT EN LAS NOTATIOS
    //PROPIAS DE CADA ENDPOINT

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity httpSecurity) throws Exception{
            return httpSecurity
                    .csrf(csrf -> csrf.disable())
                    .httpBasic(Customizer.withDefaults())
                    .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                    .build(); 
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration) throws Exception{
            return authenticationConfiguration.getAuthenticationManager();
    }

    @Bean
    public AuthenticationProvider authenticationProvider(UserDetailServiceImpl userDetailService){
        DaoAuthenticationProvider provider = new DaoAuthenticationProvider();
        provider.setPasswordEncoder(passwordEncoder());
        provider.setUserDetailsService(userDetailService);
        return provider;
    }


    @Bean
    public PasswordEncoder passwordEncoder(){
        return NoOpPasswordEncoder.getInstance();
    }
}
