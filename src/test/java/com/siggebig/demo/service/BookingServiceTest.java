package com.siggebig.demo.service;

import com.siggebig.demo.Exception.AuthenticationFailedException;
import com.siggebig.demo.Exception.EntityNotFoundException;
import com.siggebig.demo.Exception.InvalidPaymentException;
import com.siggebig.demo.controllers.BookingController;
import com.siggebig.demo.models.Booking;
import com.siggebig.demo.models.Payment;
import com.siggebig.demo.models.Trip;
import com.siggebig.demo.models.User;
import com.siggebig.demo.repository.BookingRepository;
import com.siggebig.demo.repository.PaymentRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@SpringBootTest
@ExtendWith(MockitoExtension.class)
class BookingServiceTest {

    @InjectMocks
    private BookingService bookingService;

    @Mock
    private JwtService jwtService;

    @Mock
    private UserService userService;

    @Mock
    private TripService tripService;

    @Mock
    private PaymentService paymentService;

    @Mock
    private PaymentRepository paymentRepository;

    @Mock
    private BookingRepository bookingRepository;

    @Test
    public void testCreateBookingWithTokenAndId() {



        String username = "testuser";


        when(jwtService.getUsernameFromToken("token")).thenReturn(username);


        User user = new User();
        when(userService.findByUsername(username)).thenReturn(user);


        Trip trip = new Trip();
        when(tripService.findById(1L)).thenReturn(Optional.of(trip));

        when(paymentService.validatePayment(any())).thenReturn(true);


        Booking booking = bookingService.createBookingWithTokenAndId(1L, "token");


        assertNotNull(booking);
        assertEquals(user, booking.getUser());
        assertEquals(trip, booking.getTrip());


        verify(paymentRepository).save(any(Payment.class));
        verify(bookingRepository).save(any(Booking.class));
    }

    @Test
    public void testCreateBookingWithTokenAndIdThrowsInvalidPaymentIfInvalidPayment() {

        String username = "testuser";


        when(jwtService.getUsernameFromToken("token")).thenReturn(username);


        User user = new User();
        when(userService.findByUsername(username)).thenReturn(user);


        Trip trip = new Trip();
        when(tripService.findById(1L)).thenReturn(Optional.of(trip));


        assertThrows(InvalidPaymentException.class, () -> {
            bookingService.createBookingWithTokenAndId(1L,"token");
        });

    }


  @Test
    void deleteByIdAndTokenThrowsEntityNotFoundIfNoTripExistWithThatId() {
      assertThrows(EntityNotFoundException.class, () -> {
          bookingService.deleteBookingByIdAndToken(3234L,"token");
      });
  }

    @Test
    void deleteByIdAndTokenThrowsAuthFailedIfUsernameDontMatch() {



        User user = User.builder()
                .username("user")
                .password("pass")
                .role("USER")
                .build();

        User user2 = User.builder()
                .username("user2")
                .password("pass")
                .build();



        Booking booking = new Booking();
        booking.setUser(user2);
        booking.setId(3234L);

        when(bookingRepository.findById(3234L)).thenReturn(Optional.of(booking));
        when(jwtService.getUsernameFromToken("token")).thenReturn(user.getUsername());
        when(userService.findByUsername(user.getUsername())).thenReturn(user);


        assertThrows(AuthenticationFailedException.class, () -> {
            bookingService.deleteBookingByIdAndToken(3234L,"token");
        });
    }

    @Test
    void deleteByIdAndTokenThrowsAuthFailedNoUserFromToken() {
        User user2 = User.builder()
                .username("user2")
                .password("pass")
                .build();
        Booking booking = new Booking();
        booking.setUser(user2);
        booking.setId(3234L);

        when(bookingRepository.findById(3234L)).thenReturn(Optional.of(booking));

        assertThrows(AuthenticationFailedException.class, () -> {
            bookingService.deleteBookingByIdAndToken(3234L,"token");
        });
    }

    @Test
    void verifyDeleteIfUsernameMatchWithIdAndToken() {
        User user = User.builder()
                .username("user")
                .password("pass")
                .role("USER")
                .build();

        Booking booking = new Booking();
        booking.setUser(user);
        booking.setId(3234L);

        when(bookingRepository.findById(3234L)).thenReturn(Optional.of(booking));
        when(jwtService.getUsernameFromToken("token")).thenReturn(user.getUsername());
        when(userService.findByUsername(user.getUsername())).thenReturn(user);

        bookingService.deleteBookingByIdAndToken(3234L,"token");

        verify(bookingRepository).deleteById(3234L);


    }

    @Test
    void deleteByIdAndTokenIfUsernameDontMatchButAdminVerifyDelete() {

        User user = User.builder()
                .username("user")
                .password("pass")
                .role("ADMIN")
                .build();

        User user2 = User.builder()
                .username("user2")
                .password("pass")
                .build();


        Booking booking = new Booking();
        booking.setUser(user2);
        booking.setId(3234L);

        when(bookingRepository.findById(3234L)).thenReturn(Optional.of(booking));
        when(jwtService.getUsernameFromToken("token")).thenReturn(user.getUsername());
        when(userService.findByUsername(user.getUsername())).thenReturn(user);


        bookingService.deleteBookingByIdAndToken(3234L,"token");

        verify(bookingRepository).deleteById(3234L);
    }



}
