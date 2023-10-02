package com.siggebig.demo.controllers;

import com.siggebig.demo.DTO.LoginDto;
import com.siggebig.demo.models.Trip;
import com.siggebig.demo.models.User;
import com.siggebig.demo.repository.TripRepository;
import com.siggebig.demo.repository.UserRepository;
import com.siggebig.demo.service.BookingService;
import com.siggebig.demo.service.JwtService;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;


@AutoConfigureMockMvc
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD) // fixes bug i did not quite understand
@SpringBootTest
class BookingControllerEndToEndTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private BookingService bookingService;

    @Autowired
    private JwtService jwtService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private TripRepository tripRepository;

    @Test
    void createBookingReturnsBookingIfSuccess() throws Exception {
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
                .andExpect(jsonPath("$.booking.user.username").value("user"))
                .andExpect(jsonPath("$.booking.trip.departure").value("Orust"))
                .andExpect(jsonPath("$.booking.payment.amount").value("90")); //100*0,9=90


    }

    @Test
    void createBookingReturns400IfInvalidInfo() {

    }



}
