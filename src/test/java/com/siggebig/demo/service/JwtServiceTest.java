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
    void getTokenReturnsTokenIfAuthIsTrue() {
        LoginDto auth = LoginDto.builder()
                .username("sean")
                .password("123").build();


        // skips to validate test to database
        when(authService.authenticate(auth))
                .thenReturn(true);

        var token= jwtService.getToken(auth);

        assertNotNull(token);
    }

    @Test
    void getTokenReturnsNullOfAuthIsFalse() {
        LoginDto auth = LoginDto.builder()
                .username("sean")
                .password("123").build();

        when(authService.authenticate(auth))
                .thenReturn(false);

        var token = jwtService.getToken(auth);

        assertNull(token);

    }

    @Test
    void verifyTokenReturnsTrueOfTokenIsCorrect() {
        LoginDto auth = LoginDto.builder()
                .username("sean")
                .password("123").build();

        when(authService.authenticate(auth)).thenReturn(true);

        String token = jwtService.getToken(auth);

        assertTrue(jwtService.verifyToken(token,auth.getUsername()));



    }

    @Test
    void verifyTokenReturnsFalseOfTokenIsInvalid() {
        assertFalse(jwtService.verifyToken("invalidToken","randomUsername"));
    }


    @Test
    void getUsernameFromTokenReturnsUsernameIfValid() {
        LoginDto auth = LoginDto.builder()
                .username("sean")
                .password("123").build();

        when(authService.authenticate(auth)).thenReturn(true);

        String token = jwtService.getToken(auth);

        String usernameFromToken = jwtService.getUsernameFromToken(token);

        assertNotNull(usernameFromToken);
        assertEquals(auth.getUsername(),usernameFromToken);

    }

    @Test
    void getUsernameFromTokenReturnsNullIfTokenIsInvalid() {
        LoginDto auth = LoginDto.builder()
                .username("sean")
                .password("wrongpassword").build();

        String token = jwtService.getToken(auth);

        String usernameFromToken = jwtService.getUsernameFromToken(token);

        assertNull(usernameFromToken);

    }

}