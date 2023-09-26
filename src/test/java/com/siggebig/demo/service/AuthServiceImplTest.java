package com.siggebig.demo.service;

import com.siggebig.demo.DTO.LoginDto;
import com.siggebig.demo.models.User;
import com.siggebig.demo.repository.UserRepository;
import lombok.extern.java.Log;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@SpringBootTest
@ExtendWith(MockitoExtension.class)
class AuthServiceImplTest {

    @InjectMocks
    private AuthServiceImpl authService;

    @Mock
    private UserRepository userRepository;

    @Test
    void testAuthenticateValidUser() {


        LoginDto loginDto = LoginDto.builder()
                .username("testuser")
                .password("password")
                .build();

        User user = User.builder()
                .username("testuser")
                .password("password")
                .build();

        // mock
        when(userRepository.findByUsername("testuser")).thenReturn(user);

        boolean result = authService.authenticate(loginDto);

        // check so result is true
        assertTrue(result);

        // check that findByUsername was called with right username
        verify(userRepository).findByUsername("testuser");
    }

    @Test
    void testAuthenticateInvalidUser() {

        LoginDto loginDto = LoginDto.builder()
                .username("testuser")
                .password("wrongpassword")
                .build();

        // Mock
        when(userRepository.findByUsername("testuser")).thenReturn(null);


        boolean result = authService.authenticate(loginDto);

        // check result is false
        assertFalse(result);

        // check that findByUsername was called with right username
        verify(userRepository).findByUsername("testuser");
    }
}