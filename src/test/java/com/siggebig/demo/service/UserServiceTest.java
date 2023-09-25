package com.siggebig.demo.service;

import com.siggebig.demo.models.User;
import com.siggebig.demo.repository.UserRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;


//@SpringBootTest
@ExtendWith(MockitoExtension.class)
class UserServiceTest {


    @Mock
    private UserRepository userRepository;


    @InjectMocks
    private UserService userService;


    @Test
    void createUserReturnsUserInfo() {

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

        Mockito.when(userRepository.existsByUsername("testUsername")).thenReturn(true);

        boolean result = userRepository.existsByUsername(username);

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
    void updateUser() {
    }

    @Test
    void deleteUserById() {
    }

    @Test
    void findById() {
    }

    @Test
    void updateUserById() {
    }
}