package com.siggebig.demo.repository;

import com.siggebig.demo.models.User;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.jdbc.EmbeddedDatabaseConnection;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.assertj.core.api.Assertions;
import org.junit.Assert;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@AutoConfigureTestDatabase(connection = EmbeddedDatabaseConnection.H2)
public class UserRepositoryTests {

    @Autowired
    private UserRepository userRepository;


    @Test
    void findByIdReturnsUserWithThatId() {
        //arrange
        User user = User.builder()
                .username("mockuser")
                .password("password")
                .build();
        //act
        User savedUser = userRepository.save(user);
        Optional<User> dbUser = userRepository.findById(savedUser.getId());


        //assert
        assertNotNull(dbUser);
        assertEquals(savedUser.getId(),dbUser.get().getId());
        assertEquals(savedUser.getUsername(),dbUser.get().getUsername());
        assertEquals(savedUser.getPassword(),dbUser.get().getPassword());

    }

    @Test
    void existsByIdReturnsTrueIfUserExistsAndFalseIfUserDontExist() {
        //arrange
        User user = User.builder()
                .id(3L)
                .username("mockuser")
                .password("password")
                .role("USER")
                .build();

        assertFalse(userRepository.existsById(user.getId()));


        User savedUser = userRepository.save(user);

        assertTrue(userRepository.existsById(savedUser.getId()));


    }

    @Test
    void existsByUsernameReturnsTrueIfUserExistsAndFalseIfUserDontExist() {
        //arrange
        User user = User.builder()
                .username("mockuser")
                .password("password")
                .role("USER")
                .build();

        assertFalse(userRepository.existsByUsername(user.getUsername()));

        User savedUser = userRepository.save(user);


        assertTrue(userRepository.existsByUsername(savedUser.getUsername()));
        assertTrue(userRepository.existsByUsername("mockuser"));


    }



}
