package com.siggebig.demo.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.siggebig.demo.DTO.LoginDto;
import com.siggebig.demo.models.User;
import com.siggebig.demo.repository.UserRepository;
import com.siggebig.demo.service.JwtService;
import com.siggebig.demo.service.UserService;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.http.MediaType;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;


// are these EndToEnd tests or integration tests?

@SpringBootTest
@AutoConfigureMockMvc
//@ExtendWith(MockitoExtension.class)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD) //this fixes bug where ID got higher than it should be in tests
class UserControllerEndToEndTest {



    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserRepository userRepository;


    @Autowired
    private JwtService jwtService;


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
    void getAllUsersReturnsUsersAndOk() throws Exception {

        User user = User.builder()
                .id(1L)
                .username("user1")
                .password("password")
                .email("fake@mail.com")
                .build();
        User user2 = User.builder()
                .id(2L)
                .username("fakeuser")
                .password("password")
                .email("fake@mail.com")
                .role("USER")
                .build();
        userRepository.save(user);
        userRepository.save(user2);


        mockMvc.perform(get("/user")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].username").value("user1"))
                .andExpect(jsonPath("$[1].username").value("fakeuser"));

    }

    @Test
    void getAllUsersReturns204WhenNoUsers() throws Exception {



        mockMvc.perform(get("/user")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().is(204))
                .andExpect(header().string("x-info", "No users found in db"));

    }

    @Test //fråga jakob why no work work
    void getUserByIdReturnsCorrectUser() throws Exception {
        User user = User.builder()
                .username("user1")
                .password("password")
                .email("fake@mail.com")
                .build();
        User user2 = User.builder()
                .username("fakeuser")
                .password("password")
                .email("fake@mail.com")
                .role("USER")
                .build();
        userRepository.save(user);
        userRepository.save(user2);


        mockMvc.perform(get("/user/2")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.username").value("fakeuser"))
                .andExpect(jsonPath("$.id").value(2));


    }

    @Test
    void getUserByIdReturns204IfUserNotFound() throws Exception {

        mockMvc.perform(get("/user/66")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().is(204))
                .andExpect(header().string("x-info", "No user with that id"));
    }


    @Test
    void deleteUserWithTokenReturnsOkIfSuccess() throws Exception {


        User user2 = User.builder()
                .id(2L)
                .username("fakeuser")
                .password("password")
                .email("fake@mail.com")
                .role("USER")
                .build();
        userRepository.save(user2);


                    //this is user2
                LoginDto loginDto = LoginDto.builder()
                        .username("fakeuser")
                        .password("password")
                        .build();

                String token = jwtService.getToken(loginDto);



                mockMvc.perform(delete("/user/delete")
                                .header("JWTToken", token))
                                .andExpect(status().isOk())
                                .andExpect(content().string("User deleted successfully"));

    }


    @Test
    void deleteUserWithTokenReturns404IfTokenInvalid() throws Exception {

        mockMvc.perform(delete("/user/delete")
                        .header("JWTToken", "invalidtoken"))
                .andExpect(status().is(404))
                .andExpect(content().string("User not found"));
    }


    @Test // why dis not work?
    void updateUserWithTokenReturnsOkAndUpdatedUserIfSuccess() throws Exception {

        User user = User.builder()
                .id(1L)
                .username("user1")
                .password("password")
                .email("fake@mail.com")
                .build();
        User user2 = User.builder()
                .id(2L)
                .username("fakeuser")
                .password("password")
                .email("fake@mail.com")
                .role("USER")
                .build();
        userRepository.save(user);
        userRepository.save(user2);

        User newInfo = User.builder()
                .username("newusername")
                .password("newpassword")
                .build();

        //convert object user to JSON
        ObjectMapper mapper = new ObjectMapper();
        String userJson = mapper.writeValueAsString(newInfo);

        //user2
        LoginDto loginDto = LoginDto.builder()
                .username("fakeuser")
                .password("password")
                .build();

        String token = jwtService.getToken(loginDto);


        mockMvc.perform(put("/user/update")
                .header("JWTToken", token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(userJson))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value("2"))
                .andExpect(jsonPath("$.role").value("USER"))
                .andExpect(jsonPath("$.password").value("newpassword"))
                .andExpect(jsonPath("$.username").value("newusername"));


    }

    @Test
    void updateUserWithTokenReturns400IfEntityNotFound() throws Exception {

        User user = new User();

        //convert object user to JSON, if no user then request fails and returns 400 without custom header
        ObjectMapper mapper = new ObjectMapper();
        String userJson = mapper.writeValueAsString(user);

        mockMvc.perform(put("/user/update")
                        .header("JWTToken", "invalidtoken")
                .contentType(MediaType.APPLICATION_JSON)
                .content(userJson))
                .andExpect(status().is(400))
                .andExpect(header().string("x-info", "Invalid data, check new info or token"));


    }

// tester nedan är samma som test ovan, get alltid 200 http status, inte end to end tester heller


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