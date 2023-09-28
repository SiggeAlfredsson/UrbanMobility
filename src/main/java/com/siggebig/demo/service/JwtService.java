package com.siggebig.demo.service;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTVerificationException;
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

   //
    public boolean verifyToken(String token, String username) {

        try {
            JWTVerifier verifier = JWT.require(Algorithm.HMAC256("supersecret"))
                    .withClaim("username", username)
                    .build();
            verifier.verify(token);
            return true;
        } catch (JWTVerificationException e) {
            return false;
        }

    }

}
