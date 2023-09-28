package com.siggebig.demo.service;

import com.siggebig.demo.Exception.AuthenticationFailedException;
import com.siggebig.demo.Exception.EntityNotFoundException;
import com.siggebig.demo.models.User;
import com.siggebig.demo.repository.UserRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.mockito.Mockito.*;
import static org.assertj.core.api.Assertions.assertThat;


@SpringBootTest
@ExtendWith(MockitoExtension.class)
class UserServiceTest {


    @Mock
    private UserRepository userRepository;


    @InjectMocks
    private UserService userService;

    @Mock
    private JwtService jwtService;


    @Test
    void throwsEntityNotFoundWhenUserNotExist() {

        User newInfo = new User();

        Mockito.when(userRepository.existsById(3L)).thenReturn(false);


        // Act and Assert
        assertThrows(EntityNotFoundException.class, () -> {
            userService.updateUserById(3L, newInfo);
        });
    }




    // IDK bout these
    @Test
    void updateUserByIdWorks() {

        Long userId = 1L;

        User userOldInfo = User.builder()
                .id(userId)
                .username("fakeuser")
                .password("password")
                .email("fake@mail.com")
                .build();


        User userNewInfo1 = User.builder()
                .username("newusername")
                .build();




//        userId is stubbing mismatch? but 1L works
        when(userRepository.existsById(1L)).thenReturn(true);


        // mocks that auth is valid
        when(jwtService.authenticateToken("mocktoken")).thenReturn(true);

        when(userRepository.findById(userId)).thenReturn(Optional.of(userOldInfo));
        when(userRepository.save(any(User.class))).thenReturn(userNewInfo1);

        User updatedUser = userService.updateUserById(userId, userNewInfo1);

        assertEquals(userOldInfo.getId(), updatedUser.getId());
        assertEquals(userOldInfo.getEmail(), updatedUser.getEmail());
        assertEquals("newusername",updatedUser.getUsername());

    }

    @Test
    void updateUserByIdWhenUsernameIsNull() {

        Long userId = 1L;

        User userOldInfo = User.builder()
                .id(userId)
                .username("fakeuser")
                .password("password")
                .email("fake@mail.com")
                .build();


        User userNewInfo = User.builder()
                .password("newpassword")
                .build();

        when(userRepository.existsById(1L)).thenReturn(true);

        when(jwtService.authenticateToken("mocktoken")).thenReturn(true);


        when(userRepository.findById(userId)).thenReturn(Optional.of(userOldInfo));
        when(userRepository.save(any(User.class))).thenReturn(userNewInfo);

        User updatedUser = userService.updateUserById(userId, userNewInfo);

        assertEquals(userOldInfo.getId(), updatedUser.getId());
        assertEquals(userOldInfo.getEmail(), updatedUser.getEmail());
        assertEquals("newpassword",updatedUser.getPassword());


    }

    @Test
    void updateUserByIdThrowsAuthFailedExcWhenAuthIsFalse() {
        User newInfo = User.builder()
                .username("username")
                .password("newpassword")
                .build();

        when(userRepository.existsById(3L)).thenReturn(true);

        when(jwtService.authenticateToken("mocktoken")).thenReturn(false);


        assertThrows(AuthenticationFailedException.class, () -> {
            userService.updateUserById(3L, newInfo);
        });
    }

}