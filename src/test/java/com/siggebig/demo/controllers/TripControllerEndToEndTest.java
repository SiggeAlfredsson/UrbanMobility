package com.siggebig.demo.controllers;


import com.siggebig.demo.models.Trip;
import com.siggebig.demo.models.User;
import com.siggebig.demo.repository.TripRepository;
import com.siggebig.demo.repository.UserRepository;
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

import java.time.LocalDate;
import java.time.LocalTime;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
//@ExtendWith(MockitoExtension.class)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
public class TripControllerEndToEndTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private TripRepository tripRepository;

    @Autowired
    private UserRepository userRepository;

    @BeforeEach
    @Transactional
    public void setupDatabase() {
        userRepository.deleteAll();
        tripRepository.deleteAll();
    }



    @Test
    void getAllTripsReturns204IfEmpty() throws Exception {


        mockMvc.perform(get("/trip"))
                .andExpect(status().is(204))
                .andExpect(header().string("x-info","No trips found in db"));

    }

    @Test
    void getAllTripsReturnsTripsAndOk() throws Exception {
        User vasttrafik = User.builder()
                .id(1L)
                .username("VästTrafik")
                .password("password")
                .email("fake@mail.com")
                .role("SUPPLIER")
                .build();

        User sj = User.builder()
                .id(2L)
                .username("SJ")
                .password("password")
                .email("fake@mail.com")
                .role("SUPPLIER")
                .build();

        userRepository.save(vasttrafik);
        userRepository.save(sj);

        Trip trip = Trip.builder()
                .departure("Uddevalla")
                .arrival("Vänersborg")
                .transportType("Bus")
                .price(100)
                .discount(10)
                .departureDate(LocalDate.of(2023, 10,22))
                .departureTime(LocalTime.of(22,0))
                .arrivalDate(LocalDate.of(2023,10,22))
                .arrivalTime(LocalTime.of(22,30))
                .user(vasttrafik)
                .build();

        Trip trip2 = Trip.builder()
                .departure("Kungälv")
                .arrival("Göteborg")
                .transportType("Train")
                .price(60)
                .discount(10)
                .departureDate(LocalDate.of(2023, 10,20))
                .departureTime(LocalTime.of(12,0))
                .arrivalDate(LocalDate.of(2023,10,20))
                .arrivalTime(LocalTime.of(12,20))
                .user(sj)
                .build();

        tripRepository.save(trip);
        tripRepository.save(trip2);

        mockMvc.perform(get("/trip"))
                .andExpect(status().is(200))

                .andExpect(jsonPath("$[0].departure").value("Uddevalla"))
                .andExpect(jsonPath("$[0].arrival").value("Vänersborg"))
                .andExpect(jsonPath("$[0].price").value("100"))
                .andExpect(jsonPath("$[0].user.username").value("VästTrafik"))


                .andExpect(jsonPath("$[1].departure").value("Kungälv"))
                .andExpect(jsonPath("$[1].arrival").value("Göteborg"))
                .andExpect(jsonPath("$[1].price").value("60"))
                .andExpect(jsonPath("$[1].user.username").value("SJ"));




    }



}
