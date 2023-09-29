package com.siggebig.demo.controllers;

import com.siggebig.demo.DTO.LoginDto;
import com.siggebig.demo.Exception.AuthenticationFailedException;
import com.siggebig.demo.models.User;
import com.siggebig.demo.repository.UserRepository;
import com.siggebig.demo.service.AuthService;
import com.siggebig.demo.service.JwtService;
import com.siggebig.demo.service.UserService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Repository;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.naming.AuthenticationException;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/auth")
public class AuthController {

    @Autowired
    JwtService jwtService;

    @Autowired
    UserService userService;


    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginDto loginDto) {
            String token = jwtService.getToken(loginDto);
            if (token != null) {
                return ResponseEntity.ok(Map.of("token", token));
            }else {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body("Authentication failed");
            }
    }

    @PostMapping("/register")
    public ResponseEntity<String> createUser (@Valid @RequestBody User user, BindingResult result) {

        if(result.hasErrors()) {
            return ResponseEntity.badRequest().body("Not valid inputs");
        }

        if(userService.existsByUsername(user.getUsername())) {
            return ResponseEntity.badRequest().body("Username is taken");
        }

        userService.save(user);
        return ResponseEntity.ok().body("User registered");
    }



}
