package com.siggebig.demo.controllers;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.siggebig.demo.DTO.LoginDto;
import com.siggebig.demo.models.User;
import com.siggebig.demo.service.AuthService;
import com.siggebig.demo.service.JwtService;
import com.siggebig.demo.service.UserService;
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
import org.springframework.test.web.servlet.MockMvc;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(controllers = AuthController.class)
@AutoConfigureMockMvc(addFilters = false)
@ExtendWith(MockitoExtension.class)
//@SpringBootTest
class AuthControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UserService userService;

    @MockBean
    private JwtService jwtService;

    @MockBean
    private AuthService authService;



    @Test
    void testLoginWithValidCred() throws Exception {

        LoginDto loginDto = LoginDto.builder()
                .username("username")
                .password("password")
                .build();

        //convert object creds to JSON
        ObjectMapper mapper = new ObjectMapper();
        String credJson = mapper.writeValueAsString(loginDto);

//        Mockito.when(authService.authenticate(Mockito.any())).thenReturn(true);  dident get this to work so just mocked the token generation and auth
        Mockito.when(jwtService.getToken(loginDto)).thenReturn("valid_token");

        mockMvc.perform(post("/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(credJson))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").exists());
    }

    @Test
    public void testLoginWithInvalidCredentials() throws Exception {
        // Ogiltiga inloggningsuppgifter
        LoginDto invalidLoginDto = LoginDto.builder()
                .username("username")
                .password("wrongpassword")  // Använd ogiltigt lösenord
                .build();

        //convert object creds to JSON
        ObjectMapper mapper = new ObjectMapper();
        String credJson = mapper.writeValueAsString(invalidLoginDto);

        Mockito.when(authService.authenticate(invalidLoginDto)).thenReturn(false);

        mockMvc.perform(post("/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(credJson))
                .andExpect(status().isUnauthorized())
                .andExpect(content().string("Authentication failed"));
    }

    @Test
    void testCreateUser() throws Exception {
        User user = User.builder()
                .username("fakeuser")
                .password("password")
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
}