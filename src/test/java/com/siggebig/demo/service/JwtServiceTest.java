package com.siggebig.demo.service;

import com.siggebig.demo.DTO.LoginDto;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import static org.mockito.Mockito.*;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ExtendWith(MockitoExtension.class)
class JwtServiceTest {

    @Mock
    AuthService authService;

    @InjectMocks
    JwtService jwtService;

    @Test
    void getToken() {
        var auth = LoginDto.builder()
                .username("sean")
                .password("123").build();


        // skips to validate test to database
        when(authService.authenticate(auth))
                .thenReturn(true);

        var token= jwtService.getToken(auth);

        assertNotNull(token);
    }
}