package com.siggebig.demo.controllers;

import com.siggebig.demo.models.User;
import com.siggebig.demo.service.UserService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;


import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@WebMvcTest(controllers = UserController.class)
@AutoConfigureMockMvc(addFilters = false)
@ExtendWith(MockitoExtension.class)
class UserControllerTest {



    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserController userController;


    // this fixed no qualifying bean of type userService
    @MockBean
    private UserService userService;


//    Unit test ?
    @Test
    void getAllUsersReturnsOK() throws Exception {


        mockMvc.perform(get("/user")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().is2xxSuccessful());

    }




    @Test
    void getAllUsersReturnsUsers() throws Exception {
        List<User> users = new ArrayList<>();

        User user = User.builder()
                .username("user1")
                .password("password")
                .email("fake@mail.com")
                .build();
        User user2 = User.builder()
                .username("fakeuser")
                .password("password")
                .email("fake@mail.com")
                .build();

        users.add(user);
        users.add(user2);

        Mockito.when(userService.getAllUsers()).thenReturn(users);


        mockMvc.perform(get("/user")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(users.size()))
                .andExpect(jsonPath("$[0].username").value("user1"))
                .andExpect(jsonPath("$[1].username").value("fakeuser"));


    }



}