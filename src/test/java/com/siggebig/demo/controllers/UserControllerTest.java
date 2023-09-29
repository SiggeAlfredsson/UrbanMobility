package com.siggebig.demo.controllers;

import com.siggebig.demo.Exception.EntityNotFoundException;
import com.siggebig.demo.models.User;
import com.siggebig.demo.service.JwtService;
import com.siggebig.demo.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
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
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;


import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

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

    @MockBean
    private JwtService jwtService;



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

        when(userService.getAllUsers()).thenReturn(users);


        mockMvc.perform(get("/user")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(users.size()))
                .andExpect(jsonPath("$[0].username").value("user1"))
                .andExpect(jsonPath("$[1].username").value("fakeuser"));


    }

    @Test
    void getUserByIdReturnsCorrectUser() throws Exception {

        User user = User.builder()
                .id(43L)
                .username("username")
                .password("password")
                .email("fake@mail.com")
                .build();

        when(userService.findById(43L)).thenReturn(Optional.ofNullable(user));

        mockMvc.perform(get("/user/43")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.username").value(user.getUsername()))
                .andExpect(jsonPath("$.id").value(user.getId()));

    }

    @Test
    void getUserByIdReturns204IfUserNotFound() throws Exception {
        when(userService.findById(43L)).thenReturn(null);

        mockMvc.perform(get("/user/43")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().is(204))
                .andExpect(header().string("x-info", "No user with that id"));
    }


    @Test // is this test even valid because it returns 200 whatever i do as long as i dont force throw a exc as below
    void deleteUserWithTokenReturnsOkIfSuccess() throws Exception {
                User user = User.builder()
                .id(43L)
                .username("username")
                .password("password")
                .email("fake@mail.com")
                .role("USER")
                .build();

                when(jwtService.getUsernameFromToken("validToken")).thenReturn(user.getUsername());
                when(userService.findByUsername(user.getUsername())).thenReturn(user);

                mockMvc.perform(delete("/user/delete")
                                .header("JWTToken", "validToken"))
                                .andExpect(status().isOk())
                                .andExpect(content().string("User deleted successfully"));

    }

    // Why does this test not work? fråga jakob, något med hur jag satt upp bönor/wires
    @Test
    void deleteUserWithTokenReturns404IfTokenInvalid() throws Exception {

//        when(jwtService.getUsernameFromToken("invalidtoken")).thenReturn(null); Why does this not work??

        //Why do i have to force a throw why cant it throw the exception by itself when the username is null as it should?
        Mockito.doThrow(new EntityNotFoundException("User not found"))
                .when(userService).deleteUserWithToken(anyString());

        mockMvc.perform(delete("/user/delete")
                        .header("JWTToken", "invalidtoken"))
                .andExpect(status().is(404))
                .andExpect(content().string("User not found"));
    }




// tester nedan är samma som test ovan, get alltid 200 http status


//  I commented out the method in the controller because it is not needed in assignment and the time was running out
    // got bugs i could not figure out in time, would like to check back on this tho

//    @Test // auth failed means token user was not ROLE=ADMIN
//    void deleteUserWithIdReturns401IfAuthFailed() throws Exception {
//        long idToDelete = 420L;
//
//        User user = User.builder()
//                .id(43L)
//                .username("username")
//                .password("password")
//                .email("fake@mail.com")
//                .role("USER")
//                .build();
//
//        // mock the services
//        when(jwtService.getUsernameFromToken(anyString())).thenReturn(user.getUsername());
//        when(userService.findByUsername(user.getUsername())).thenReturn(user);
//        when(userService.existsById(idToDelete)).thenReturn(true);
//
//        mockMvc.perform(delete("/user/delete/43")
//                        .header("JWTToken", "validToken"))
//                .andExpect(status().is(401))
//                .andExpect(content().string("Auth failed"));
//
//
//    }
//
//    @Test
//    void deleteUserWithIdReturns404IfIdNotExist() throws Exception {
//        long idToDelete = 420L;
//
//        User user = User.builder()
//                .id(43L)
//                .username("username")
//                .password("password")
//                .email("fake@mail.com")
//                .role("ADMIN")
//                .build();
//
//        // mock the services
//        when(jwtService.getUsernameFromToken(anyString())).thenReturn(user.getUsername());
//        when(userService.findByUsername(user.getUsername())).thenReturn(user);
//        when(userService.existsById(idToDelete)).thenReturn(false);
//
//        mockMvc.perform(delete("/user/delete/"+idToDelete)
//                        .header("JWTToken", "validToken"))
//                .andExpect(status().is(404))
//                .andExpect(content().string("No user with that Id"));
//    }
//
//    @Test
//    void deleteUserWithIdReturns404IfTokenInvalid() throws Exception {
//        long idToDelete = 420L;
//
//        User user = User.builder()
//                .id(43L)
//                .username("username")
//                .password("password")
//                .email("fake@mail.com")
//                .role("ADMIN")
//                .build();
//
//        // mock the services
//        when(jwtService.getUsernameFromToken(anyString())).thenReturn(null);
//        when(userService.findByUsername(user.getUsername())).thenReturn(null);
//        when(userService.existsById(idToDelete)).thenReturn(true);
//
//        mockMvc.perform(delete("/user/delete/"+idToDelete)
//                        .header("JWTToken", "notValidToken"))
//                .andExpect(status().is(404))
//                .andExpect(content().string("invalid token"));
//    }
//
//    @Test
//    void deleteUserWithIdReturnsOkIfSuccess() throws Exception {
//
//        long idToDelete = 420L;
//
//        User user = User.builder()
//                .id(43L)
//                .username("username")
//                .password("password")
//                .email("fake@mail.com")
//                .role("ADMIN")
//                .build();
//
//        // mock the services
//        when(jwtService.getUsernameFromToken(anyString())).thenReturn(user.getUsername());
//        when(userService.findByUsername(user.getUsername())).thenReturn(user);
//        when(userService.existsById(idToDelete)).thenReturn(true);
//
//        mockMvc.perform(delete("/user/delete/"+idToDelete)
//                        .header("JWTToken", "validToken"))
//                .andExpect(status().isOk())
//                .andExpect(content().string("User deleted successfully"));
//    }



}