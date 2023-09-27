package com.siggebig.demo.service;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.siggebig.demo.DTO.LoginDto;
import com.siggebig.demo.Exception.AuthenticationFailedException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


@Service
public class JwtService {

    @Autowired(required = false) //no bean evaluation
    private AuthService authService;

    public String getToken(LoginDto loginDto) {

        if(authService.authenticate(loginDto)) {
            return JWT.create()
                    .withClaim("username", loginDto.getUsername())
                    .sign(Algorithm.HMAC256("supersecret"));
        } else {
            return null;
        }
    }

    public boolean authenticateToken(String token) {

        return true;
    }

}
