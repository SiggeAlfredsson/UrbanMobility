package com.siggebig.demo.controllers;


import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.siggebig.demo.DTO.LoginDto;
import com.siggebig.demo.models.Trip;
import com.siggebig.demo.models.User;
import com.siggebig.demo.repository.TripRepository;
import com.siggebig.demo.repository.UserRepository;
import com.siggebig.demo.service.JwtService;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
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

    //this code is not dry but i got bugs i dident have time to figure out when doing beforeeach
    // do test code even need to be dry?

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private TripRepository tripRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private JwtService jwtService;

    @BeforeEach
    @Transactional
    public void setupDatabase() {
        userRepository.deleteAll();
        tripRepository.deleteAll();
    }

    @Test
    void updateTripWithTokenReturns400IfAuthFailed() throws Exception {

        Trip trip = new Trip();


        ObjectMapper mapper = new ObjectMapper();
        String tripJson = mapper.writeValueAsString(trip);


        mockMvc.perform(put("/trip/update")
                        .header("JWTToken", "invalidtoken")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(tripJson))
                .andExpect(status().is(400))
                .andExpect(header().string("x-info", "Auth failed"));


    }

    @Test
    void updateTripWithTokenReturnsUpdatedTrip() throws Exception {
        User vasttrafik = User.builder()
                .id(1L)
                .username("VästTrafik")
                .password("password")
                .email("fake@mail.com")
                .role("SUPPLIER")
                .build();
        userRepository.save(vasttrafik);
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

        tripRepository.save(trip);

        Trip updatedTrip = Trip.builder()
                .departure("Uddevalla")
                .arrival("Vänersborg")
                .transportType("Bus")
                .price(120)
                .discount(10)
                .departureDate(LocalDate.of(2023, 10,22))
                .departureTime(LocalTime.of(22,0))
                .arrivalDate(LocalDate.of(2023,10,22))
                .arrivalTime(LocalTime.of(22,30))
                .user(vasttrafik)
                .build();

        ObjectMapper mapper = new ObjectMapper();
        mapper.registerModule(new JavaTimeModule()); // jackson?
        String tripJson = mapper.writeValueAsString(updatedTrip);

        LoginDto loginDto = LoginDto.builder()
                .username(vasttrafik.getUsername())
                .password(vasttrafik.getPassword())
                .build();

        String token = jwtService.getToken(loginDto);



        mockMvc.perform(put("/trip/update")
                        .header("JWTToken", token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(tripJson))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.departure").value("Uddevalla"))
                .andExpect(jsonPath("$.arrival").value("Vänersborg"))
                .andExpect(jsonPath("$.price").value("120")) // new info
                .andExpect(jsonPath("$.user.username").value("VästTrafik"));
    }

    @Test
    void createTripWithInvalidTokenReturns400() throws Exception {
        Trip trip = new Trip();
        String fakeToken = "skraap pappap";

        ObjectMapper mapper = new ObjectMapper();
        mapper.registerModule(new JavaTimeModule()); // jackson?
        String tripJson = mapper.writeValueAsString(trip);

        mockMvc.perform(post("/trip")
                        .header("JWTToken", fakeToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(tripJson))
                .andExpect(status().is(400));

    }

    @Test
    void createTripWithValidTokenReturnsOkAndTrip() throws Exception {
        User vasttrafik = User.builder()
                .id(1L)
                .username("VästTrafik")
                .password("password")
                .email("fake@mail.com")
                .role("SUPPLIER")
                .build();
        userRepository.save(vasttrafik);
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
                //.user(vasttrafik) User should come from the token and not in the body
                .build();

        ObjectMapper mapper = new ObjectMapper();
        mapper.registerModule(new JavaTimeModule()); // jackson?
        String tripJson = mapper.writeValueAsString(trip);

        LoginDto loginDto = LoginDto.builder()
                .username(vasttrafik.getUsername())
                .password(vasttrafik.getPassword())
                .build();

        String token = jwtService.getToken(loginDto);

        String username = jwtService.getUsernameFromToken(token);
        User supplier = userRepository.findByUsername(username);

        mockMvc.perform(post("/trip")
                .header("JWTToken", token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(tripJson))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.departure").value("Uddevalla"))
                .andExpect(jsonPath("$.arrival").value("Vänersborg"))
                .andExpect(jsonPath("$.price").value("100"))
                .andExpect(jsonPath("$.user.username").value("VästTrafik"));


    }

    @Test
    void deleteTripWithIdMatchingUsernameTokenReturnsOk() throws Exception {
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

        LoginDto loginDto = LoginDto.builder()
                        .username(sj.getUsername())
                                .password(sj.getPassword())
                                        .build();

        String token = jwtService.getToken(loginDto);

        mockMvc.perform(delete("/trip/delete/2")
                        .header("JWTToken", token))
                .andExpect(status().isOk())
                .andExpect(content().string("Trip deleted successfully"));

    }

    @Test
    void deleteTripWithIdAdminUserTokenReturnsOk() throws Exception {
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

        User adminUser = User.builder()
                .username("adminuser")
                .password("password")
                .role("ADMIN")
                .build();

        userRepository.save(adminUser);

        LoginDto loginDto = LoginDto.builder()
                .username(adminUser.getUsername())
                .password(adminUser.getPassword())
                .build();

        String token = jwtService.getToken(loginDto);

        mockMvc.perform(delete("/trip/delete/2")
                        .header("JWTToken", token))
                .andExpect(status().isOk())
                .andExpect(content().string("Trip deleted successfully"));

    }

    @Test
    void deleteTripWithIdReturns404IfInvalidId() throws Exception {

        User adminUser = User.builder()
                .username("adminuser")
                .password("password")
                .role("ADMIN")
                .build();

        userRepository.save(adminUser);

        LoginDto loginDto = LoginDto.builder()
                .username(adminUser.getUsername())
                .password(adminUser.getPassword())
                .build();

        String token = jwtService.getToken(loginDto);

        mockMvc.perform(delete("/trip/delete/2")
                        .header("JWTToken", token))
                .andExpect(status().is(404))
                .andExpect(content().string("Trip not found"));

    }

    @Test
    void deleteTripWithIdReturns401IfInvalidToken() throws Exception {
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




        String token = "faketoken";

        mockMvc.perform(delete("/trip/delete/2")
                        .header("JWTToken", token))
                .andExpect(status().is(401))
                .andExpect(content().string("Invalid token"));

    }


    @Test
    void getTripByIdReturnsCorrectTrip() throws Exception {
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

        mockMvc.perform(get("/trip/2"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.departure").value("Kungälv"))
                .andExpect(jsonPath("$.arrival").value("Göteborg"))
                .andExpect(jsonPath("$.price").value("60"))
                .andExpect(jsonPath("$.user.username").value("SJ"))
                .andExpect(jsonPath("$.id").value(2));
    }

    @Test
    void getUserByIdReturns204IfUserNotFound() throws Exception {

        mockMvc.perform(get("/trip/66")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().is(204))
                .andExpect(header().string("x-info", "No trip with that id"));
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
