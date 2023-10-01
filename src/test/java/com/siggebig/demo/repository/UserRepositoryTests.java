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
//@AutoConfigureTestDatabase(connection = EmbeddedDatabaseConnection.H2)
public class UserRepositoryTests {

    @Autowired
    private UserRepository userRepository;

    @Test
    void savesUserAndReturnsSavedDataAndGeneratedId() {
        // arrange
        User user = User.builder()
                .username("mockuser")
                .password("password")
                .build();

        //act
        User savedUser = userRepository.save(user);

        //assert
        assertNotNull(savedUser);
        assertEquals("mockuser", savedUser.getUsername());
        Assertions.assertThat(savedUser.getId()).isGreaterThan(0);


    }

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
                .build();

        assertFalse(userRepository.existsByUsername(user.getUsername()));

        User savedUser = userRepository.save(user);


        assertTrue(userRepository.existsByUsername(savedUser.getUsername()));
        assertTrue(userRepository.existsByUsername("mockuser"));


    }


    @Test
    void deleteUserByIdDeletesUser() {
        //arrange
        User user = User.builder()
                .username("mockuser")
                .password("password")
                .build();
        //act
        User savedUser = userRepository.save(user);

        //assert before delete
        assertNotNull(savedUser);
        assertNotNull(savedUser.getId());
        assertTrue(userRepository.existsById(savedUser.getId()));

        //delete
        userRepository.deleteById(savedUser.getId());

        //assert after delete
        assertFalse(userRepository.existsById(savedUser.getId()));

    }


    @Test
    void saveUserWorksAndGetAllUsersReturnsUsers() {
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

        userRepository.save(user);
        userRepository.save(user2);

        List<User> userList = userRepository.findAll();

        assertNotNull(userList);
        assertEquals(2,userList.size(), "expected 2 users");
        assertEquals(user.getUsername(), userList.get(0).getUsername());
        assertEquals(user2.getUsername(), userList.get(1).getUsername());


    }

}
