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


//Testes all logic in userService


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


        // Act and Assert
        assertThrows(EntityNotFoundException.class, () -> {
            userService.updateUserWithToken(newInfo, "token");
        });
    }




    // IDK bout these 2
    @Test
    void updateUserByTokenWorks() {


        User userOldInfo = User.builder()
                .username("fakeuser")
                .password("password")
                .email("fake@mail.com")
                .build();


        User userNewInfo = User.builder()
                .username("newusername")
                .build();



        when(jwtService.getUsernameFromToken("token")).thenReturn(userOldInfo.getUsername());
        when(userRepository.findByUsername(userOldInfo.getUsername())).thenReturn(userOldInfo);
        when(userRepository.save(any(User.class))).thenReturn(userNewInfo);

        User updatedUser = userService.updateUserWithToken( userNewInfo, "token");

        assertEquals(userOldInfo.getId(), updatedUser.getId());
        assertEquals(userOldInfo.getEmail(), updatedUser.getEmail());
        assertEquals("newusername",updatedUser.getUsername());

    }

    @Test
    void updateUserWithTokenWhenUsernameIsNull() {

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

        when(jwtService.getUsernameFromToken("token")).thenReturn(userOldInfo.getUsername());
        when(userRepository.findByUsername(userOldInfo.getUsername())).thenReturn(userOldInfo);
        when(userRepository.save(any(User.class))).thenReturn(userNewInfo);

        User updatedUser = userService.updateUserWithToken( userNewInfo, "token");

        assertEquals(userOldInfo.getId(), updatedUser.getId());
        assertEquals(userOldInfo.getEmail(), updatedUser.getEmail());
        assertEquals("newpassword",updatedUser.getPassword());


    }

    @Test
    void updateUserByIdThrowsAuthFailedExcWhenAuthIsFalse() {

        User userNewInfo = new User();



        assertThrows(EntityNotFoundException.class, () -> {
            userService.updateUserWithToken(userNewInfo, "token");
        });
    }

    @Test
    void deleteUserWithTokenThrowsEntityNotFoundWhenUserWasNotFoundFromToken() {

        assertThrows(EntityNotFoundException.class, () -> {
            userService.deleteUserWithToken("fakeToken");
        });

    }

    @Test
    void deleteUserWithTokenDeletesUser() {
        // arrange
        String token = "mockedToken";
        String username = "username";

        when(jwtService.getUsernameFromToken(token)).thenReturn(username);

        User user = new User();
        user.setId(1L);
        user.setUsername(username);
        when(userRepository.findByUsername(username)).thenReturn(user);

        // act
        userService.deleteUserWithToken(token);

        // assert
        verify(jwtService).getUsernameFromToken(token);
        verify(userRepository).findByUsername(username);
        verify(userRepository).deleteById(1L);

    }

    @Test
    void deleteUserByIdAndTokenThrowsExceptionIfTokenIsFalse() {
        assertThrows(EntityNotFoundException.class, () -> {
            userService.deleteUserByIdAndToken(4L,"fakeToken");
        });
    }

    @Test
    void testDeleteUserByIdAndTokenSuccess() {

        String token = "mockedToken";

        long idToDelete = 420L;


        User user = User.builder()
                .id(1L)
                .username("administrator")
                .role("ADMIN")
                .build();

        when(jwtService.getUsernameFromToken(token)).thenReturn(user.getUsername());



        when(userService.findByUsername(user.getUsername())).thenReturn(user);
        when(userService.existsById(idToDelete)).thenReturn(true);

        userService.deleteUserByIdAndToken(idToDelete, token);

        verify(jwtService).getUsernameFromToken(token);
        verify(userRepository).findByUsername(user.getUsername());
        verify(userRepository).deleteById(idToDelete);
    }

    @Test
    void deleteUserByIdAndTokenThrowsAuthExceptionIfRoleIsNotAdmin() {

        String token = "mockedToken";

        User user = User.builder()
                .id(1L)
                .username("userusername")
                .role("USER")
                .build();

        when(jwtService.getUsernameFromToken(token)).thenReturn(user.getUsername());



        when(userService.findByUsername(user.getUsername())).thenReturn(user);
        when(userService.existsById(4L)).thenReturn(true);


        assertThrows(AuthenticationFailedException.class, () -> {
            userService.deleteUserByIdAndToken(4L,token);
        });

    }


    @Test
    void deleteUserByIdAndTokenThrowsEntityNotFoundExceptionIfUserIdDoesNotExist() {

        String token = "mockedToken";

        User user = User.builder()
                .id(1L)
                .username("userusername")
                .role("USER")
                .build();

        when(jwtService.getUsernameFromToken(token)).thenReturn(user.getUsername());



        when(userRepository.findByUsername(user.getUsername())).thenReturn(user);
        when(userService.existsById(69L)).thenReturn(false);


        // Act
        assertThrows(EntityNotFoundException.class, () -> {
            userService.deleteUserByIdAndToken(69L,token);
        });

    }


}