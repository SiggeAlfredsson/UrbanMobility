package com.siggebig.demo.controllers;


import com.siggebig.demo.DTO.LoginDto;
import com.siggebig.demo.models.Trip;
import com.siggebig.demo.models.User;
import com.siggebig.demo.repository.TripRepository;
import com.siggebig.demo.repository.UserRepository;
import com.siggebig.demo.service.BookingService;
import com.siggebig.demo.service.JwtService;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
//@ExtendWith(MockitoExtension.class)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD) //this fixes bug where ID got higher than it should be in tests
class BookingControllerEndToEndTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private JwtService jwtService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private TripRepository tripRepository;

    @BeforeEach
    @Transactional
    public void setupDatabase() {

        userRepository.deleteAll();
        tripRepository.deleteAll();

    }

//    @AfterEach
//    @Transactional
//    @DirtiesContext // signals that the context should be dirtied (reloaded) after the test method.. twice?
//    public void cleanupDatabase() {
//        userRepository.deleteAll();
//        tripRepository.deleteAll();
//    }

    @Test
    void createBookingReturnsBookingIfSuccess() throws Exception { // this test gave me headache..
        User user = User.builder()
                .username("user")
                .password("pass")
                .role("USER")
                .build();
        userRepository.save(user);

        Trip trip = Trip.builder()
                .id(1L)
                .departure("Orust")
                .price(100)
                .discount(10)
                .build();
        tripRepository.save(trip);


        LoginDto loginDto = LoginDto.builder()
                .username(user.getUsername())
                .password(user.getPassword())
                .build();
        String token = jwtService.getToken(loginDto);

        mockMvc.perform(post("/trip/book/1")
                .header("JWTToken", token))
                .andExpect(status().isOk())
                .andDo(print())
                .andExpect(jsonPath("$.id").value("1"))
                .andExpect(jsonPath("$.user.username").value("user"))
                .andExpect(jsonPath("$.trip.departure").value("Orust"))
                .andExpect(jsonPath("$.payment.amount").value("90.0")); //100*0,9=90


    }

    @Test
    void createBookingReturns400IfInvalidToken() throws Exception {


        mockMvc.perform(post("/trip/book/1")
                        .header("JWTToken", "fakeToken"))
                .andExpect(status().is(400));

    }

    @Test
    void createBookingReturns400IfIvalidId() throws Exception {
        User user = User.builder()
            .username("user")
            .password("pass")
            .role("USER")
            .build();
        userRepository.save(user);
        LoginDto loginDto = LoginDto.builder()
                .username(user.getUsername())
                .password(user.getPassword())
                .build();
        String token = jwtService.getToken(loginDto);

        mockMvc.perform(post("/trip/book/43")
                        .header("JWTToken", token))
                .andExpect(status().is(400));

    }



}
