package com.siggebig.demo.service;

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

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    void throwsEntityNotFoundWhenUserNotExist() {
        long userId = 3L;
        User newInfo = new User();

        Mockito.when(userRepository.existsById(3L)).thenReturn(false);


        // Act and Assert
        assertThrows(EntityNotFoundException.class, () -> {
            userService.updateUserById(userId, newInfo);
        });
    }


    @Test
    void saveUserReturnsUserInfo() {

        User user = User.builder()
                .username("fakeuser")
                .password("password")
                .email("fake@mail.com")
                .build();

        when(userRepository.save(Mockito.any(User.class))).thenReturn(user);

        User savedUser = userService.save(user);

        assertNotNull(user);
        assertEquals(user.getUsername(),savedUser.getUsername());

    }

    @Test
    void existsByUsernameReturnsTrueIfUsernameExists() {
        String username = "testUsername";

        Mockito.when(userRepository.existsByUsername(username)).thenReturn(true);

        boolean result = userService.existsByUsername(username);

        assertTrue(result);
    }


    @Test
    void existsByIdReturnsTrueIfUserExists() {


        Mockito.when(userRepository.existsById(1L)).thenReturn(true);

        boolean result = userService.existsById(1L);

        assertTrue(result);
    }

    @Test
    void getAllUsersReturnsUsers() throws Exception {
        List<User> users = new ArrayList<>();

        User user = User.builder()
                .username("fakeuser")
                .password("password")
                .email("fake@mail.com")
                .build();
        User user2 = User.builder()
                .username("fakeuser2")
                .password("password")
                .email("fake@mail.com")
                .build();

        users.add(user);
        users.add(user2);

        Mockito.when(userRepository.findAll()).thenReturn(users);

        List<User> savedUsers = userService.getAllUsers();

        assertNotNull(savedUsers);
        assertEquals(2,savedUsers.size(), "expected 2 users");
        assertEquals(user.getUsername(), savedUsers.get(0).getUsername());
        assertEquals(user2.getUsername(), savedUsers.get(1).getUsername());


    }

    @Test
    void deleteUserById() {
        long userId = 1L;


        // Act
        userService.deleteUserById(userId);

        // Assert
        verify(userRepository, times(1)).deleteById(userId);




    }

    @Test
    void findById() {

        Long userId = 1L;

        User userForMock = User.builder()
                .id(userId)
                .username("fakeuser")
                .password("password")
                .email("fake@mail.com")
                .build();

        doReturn(Optional.of(userForMock)).when(userRepository).findById(userId);

        Optional<User> userFromService = userService.findById(userId);

        assertNotNull(userFromService, "User not found with id " + userId);
        assertEquals(userId, userFromService.get().getId());
        assertEquals(userForMock.getUsername(), userFromService.get().getUsername());

// Could do for each column if wanted to




    }

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

        when(userRepository.findById(userId)).thenReturn(Optional.of(userOldInfo));
        when(userRepository.save(any(User.class))).thenReturn(userNewInfo1);

        User updatedUser = userService.updateUserById(userId, userNewInfo1);

        assertEquals(userOldInfo.getId(), updatedUser.getId());
        assertEquals(userOldInfo.getEmail(), updatedUser.getEmail());
        assertEquals("newusername",updatedUser.getUsername());

    }
// IDK bout these
    @Test
    void updateUserByIdWhenUsernameIsNull() {

        Long userId = 1L;

        User userOldInfo = User.builder()
                .id(userId)
                .username("fakeuser")
                .password("password")
                .email("fake@mail.com")
                .build();


        User userNewInfo1 = User.builder()
                .password("newpassword")
                .build();

        when(userRepository.existsById(1L)).thenReturn(true);

        when(userRepository.findById(userId)).thenReturn(Optional.of(userOldInfo));
        when(userRepository.save(any(User.class))).thenReturn(userNewInfo1);

        User updatedUser = userService.updateUserById(userId, userNewInfo1);

        assertEquals(userOldInfo.getId(), updatedUser.getId());
        assertEquals(userOldInfo.getEmail(), updatedUser.getEmail());
        assertEquals("newpassword",updatedUser.getPassword());


    }

}