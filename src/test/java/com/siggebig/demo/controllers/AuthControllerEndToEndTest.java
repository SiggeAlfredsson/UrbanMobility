package com.siggebig.demo.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.siggebig.demo.DTO.LoginDto;
import com.siggebig.demo.models.User;
import com.siggebig.demo.repository.UserRepository;
import com.siggebig.demo.service.AuthService;
import com.siggebig.demo.service.JwtService;
import com.siggebig.demo.service.UserService;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.web.servlet.MockMvc;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;


// are these EndToEnd tests or integration tests?

@AutoConfigureMockMvc
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD) // fixes bug i did not quite understand
@SpringBootTest
class AuthControllerEndToEndTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserRepository userRepository;

    @BeforeEach
    @Transactional
    public void setupDatabase() {
        userRepository.deleteAll();
    }

    @AfterEach
    @Transactional
    @DirtiesContext // signals that the context should be dirtied (reloaded) after the test method
    public void cleanupDatabase() {
        userRepository.deleteAll();
    }





    @Test
    void testLoginWithValidCred() throws Exception {

        User user = User.builder()
                .id(1L)
                .username("username")
                .password("password")
                .role("USER")
                .email("fake@mail.com")
                .build();

        userRepository.save(user);

        LoginDto loginDto = LoginDto.builder()
                .username("username")
                .password("password")
                .build();

        //convert object creds to JSON
        ObjectMapper mapper = new ObjectMapper();
        String credJson = mapper.writeValueAsString(loginDto);


        mockMvc.perform(post("/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(credJson))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").exists());
    }

    @Test
    void testLoginWithInvalidCredentials() throws Exception {

        User user = User.builder()
                .id(1L)
                .username("username")
                .password("password")
                .email("fake@mail.com")
                .role("USER")
                .build();

        userRepository.save(user);

        LoginDto invalidLoginDto = LoginDto.builder()
                .username("username")
                .password("wrongpassword")  // not valid
                .build();

        //convert object creds to JSON
        ObjectMapper mapper = new ObjectMapper();
        String credJson = mapper.writeValueAsString(invalidLoginDto);


        mockMvc.perform(post("/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(credJson))
                .andExpect(status().isUnauthorized())
                .andExpect(content().string("Authentication failed"));
    }


    @Test
    void testCreateUserValidInputs() throws Exception {
        User user = User.builder()
                .username("fakeuser")
                .password("password")
                .role("USER")
                .build();

        //convert object user to JSON
        ObjectMapper mapper = new ObjectMapper();
        String userJson = mapper.writeValueAsString(user);


        mockMvc.perform(post("/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(userJson))
                .andExpect(status().isOk())
                .andExpect(content().string("User registered"));


    }

    @Test
    void testCreateUserInvalidInputs() throws Exception {

        // password is required to create a user
        User user = User.builder()
                .role("USER")
                .build();

        //convert object user to JSON
        ObjectMapper mapper = new ObjectMapper();
        String userJson = mapper.writeValueAsString(user);


        mockMvc.perform(post("/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(userJson))
                .andExpect(status().is(400))
                .andExpect(content().string("Not valid inputs"));

    }

    @Test
    void testCreateUserInvalidUsername() throws Exception {

        User user2 = User.builder()
                .username("username")
                .password("password")
                .email("fake@mail.com")
                .role("USER")
                .build();

        userRepository.save(user2);

        User user = User.builder()
                .username("username") //username is already taken in db
                .password("password")
                .role("USER")
                .build();

        //convert object user to JSON
        ObjectMapper mapper = new ObjectMapper();
        String userJson = mapper.writeValueAsString(user);


        mockMvc.perform(post("/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(userJson))
                .andExpect(status().is(400))
                .andExpect(content().string("Username is taken"));

    }

}