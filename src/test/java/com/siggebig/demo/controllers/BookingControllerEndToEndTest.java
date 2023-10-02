package com.siggebig.demo.controllers;


import com.siggebig.demo.DTO.LoginDto;
import com.siggebig.demo.models.Booking;
import com.siggebig.demo.models.Payment;
import com.siggebig.demo.models.Trip;
import com.siggebig.demo.models.User;
import com.siggebig.demo.repository.BookingRepository;
import com.siggebig.demo.repository.TripRepository;
import com.siggebig.demo.repository.UserRepository;
import com.siggebig.demo.service.JwtService;
import com.siggebig.demo.service.PaymentService;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;


import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.web.servlet.MockMvc;


import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.mockito.Mockito.*;


@SpringBootTest
@AutoConfigureMockMvc
@ExtendWith(MockitoExtension.class)
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

    @Autowired
    private BookingRepository bookingRepository;


//
//    @InjectMocks
//    private PaymentService paymentService;

    @MockBean
    private PaymentService paymentService;

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

        when(paymentService.validatePayment(any())).thenReturn(true);


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
    void createBookingReturns400IfInvalidPayment() throws Exception { // this test gave me headache..
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

        //when(paymentService.validatePayment(any())).thenReturn(false); //this always returns false so no need to mock right now.

        mockMvc.perform(post("/trip/book/1")
                        .header("JWTToken", token))
                .andExpect(status().is(400))
                .andExpect(header().string("x-info","Invalid payment"));


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


    @Test
    void deleteBookingReturnsOkIfSuccess() throws Exception {
        User user = User.builder()
                .username("user")
                .password("pass")
                .role("USER")
                .build();
        userRepository.save(user);

        Booking booking = new Booking();
        booking.setId(1L);
        booking.setUser(user);
        bookingRepository.save(booking);

        LoginDto loginDto = LoginDto.builder()
                .username(user.getUsername())
                .password(user.getPassword())
                .build();
        String token = jwtService.getToken(loginDto);


        mockMvc.perform(delete("/trip/book/1")
                        .header("JWTToken", token))
                .andExpect(status().isOk())
                .andExpect(content().string("Booking deleted"));

    }

    @Test
    void deleteBookingReturns404IfNoUserExistWithThatId() throws Exception {
        mockMvc.perform(delete("/trip/book/1")
                        .header("JWTToken", "token"))
                .andExpect(status().is(404))
                .andExpect(content().string("No booking with that id"));
    }

    @Test
    void deleteBookingReturns401IfInvalidToken() throws Exception {

        Booking booking = new Booking();
        booking.setId(1L);
        bookingRepository.save(booking);



        mockMvc.perform(delete("/trip/book/1")
                        .header("JWTToken", "invalidToken"))
                .andExpect(status().is(401))
                .andExpect(content().string("Invalid Token"));

    }


}
