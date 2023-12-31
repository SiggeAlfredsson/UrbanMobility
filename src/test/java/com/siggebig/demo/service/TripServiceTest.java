package com.siggebig.demo.service;


import com.siggebig.demo.Exception.AuthenticationFailedException;
import com.siggebig.demo.Exception.EntityNotFoundException;
import com.siggebig.demo.models.Trip;
import com.siggebig.demo.models.User;
import com.siggebig.demo.repository.TripRepository;
import com.siggebig.demo.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.*;


import static org.junit.jupiter.api.Assertions.assertThrows;

// test for logic in tripService

@SpringBootTest
@ExtendWith(MockitoExtension.class)
public class TripServiceTest {

    @Mock
    private TripRepository tripRepository;

    @InjectMocks
    private TripService tripService;

    @Mock
    private JwtService jwtService;

    @Mock
    private UserService userService;


    @Test
    void updateTripWithTokenThrowsAuthFailedExcIfInvalidToken() {
        Trip trip = new Trip();
        assertThrows(AuthenticationFailedException.class, () -> {
            tripService.createTripWithToken(trip, "fakeToken");
        });
    }

    @Test
    void updateTripWithTokenThrowsAuthFailedExcIfTokenDontMatch() {
        User user = User.builder()
                .username("user")
                .password("pass")
                .role("USER")
                .build();


        User supplier = User.builder()
                .username("notuser")
                .password("pass")
                .build();

        Trip trip = Trip.builder()
                .user(supplier)
                .build();


        when(jwtService.getUsernameFromToken("validToken")).thenReturn(user.getUsername());
        when(userService.findByUsername(user.getUsername())).thenReturn(user);



        assertThrows(AuthenticationFailedException.class, () -> {
            tripService.updateTripWithToken(trip, "validToken");
        });
    }

    @Test
    void updateTripWithTokenVerifySaveIfUserIsAdmin() {
        User user = User.builder()
                .username("user")
                .password("pass")
                .role("ADMIN")
                .build();


        User supplier = User.builder()
                .username("notuser")
                .password("pass")
                .build();

        Trip trip = Trip.builder()
                .user(supplier)
                .build();


        when(jwtService.getUsernameFromToken("validToken")).thenReturn(user.getUsername());
        when(userService.findByUsername(user.getUsername())).thenReturn(user);

        Trip trip1 = tripService.updateTripWithToken(trip, "validToken");

        assertNotNull(trip1);
        verify(tripRepository).save(trip);


    }

    @Test
    void verifyUpdateTripIfValidToken() {
        User supplier = new User(); //idk why i dont user builder
        supplier.setUsername("username");
        supplier.setPassword("password");
        supplier.setRole("SUPPLIER");
        Trip trip = new Trip();
        trip.setUser(supplier);

        when(jwtService.getUsernameFromToken("validToken")).thenReturn(supplier.getUsername());
        when(userService.findByUsername(supplier.getUsername())).thenReturn(supplier);

        Trip trip1 = tripService.updateTripWithToken(trip, "validToken");

        assertNotNull(trip1);
        verify(tripRepository).save(trip);
    }

    @Test
    void verifySaveTripIfValidToken() {
        User supplier = new User(); //idk why i dont user builder
        supplier.setUsername("username");
        supplier.setPassword("password");
        supplier.setRole("SUPPLIER");
        Trip trip = new Trip();

        when(jwtService.getUsernameFromToken("validToken")).thenReturn(supplier.getUsername());
        when(userService.findByUsername(supplier.getUsername())).thenReturn(supplier);

        tripService.createTripWithToken(trip, "validToken");

        verify(tripRepository).save(trip);
    }
    @Test
    void createTripWithTokenThrowAuthFailedIfInvalidToken() {
        Trip trip = new Trip();
        assertThrows(AuthenticationFailedException.class, () -> {
            tripService.createTripWithToken(trip, "fakeToken");
        });
    }

    @Test
    void deleteTripWithIdAndTokenThrowsEntityNotFoundIfTripNotExist() {

        User user = User.builder()
                        .username("username")
                                .password("password")
                                        .build();

        when(jwtService.getUsernameFromToken("validToken")).thenReturn("username");
        when(userService.findByUsername("username")).thenReturn(user);


        assertThrows(EntityNotFoundException.class, () -> {
            tripService.deleteTripWithIdAndToken(3L,"validToken");
        });
    }

    //invalid token means that username dont match the trip supplier or role is not admin, admin can change every trip
    @Test
    void deleteTripWithIdAndTokenThrowsAuthFailedIfTokenIsInvalid() {



        assertThrows(AuthenticationFailedException.class, () -> {
            tripService.deleteTripWithIdAndToken(3L,"fakeToken");
        });
    }

    @Test
    void deleteTripWithIdAndTokenThrowsAuthFailedIfTokenIfUsernameDontMatchSupplier() {
        User user = User.builder()
                .username("username")
                .password("password")
                .role("USER")
                .build();

        User invalidUser = User.builder()
                .username("notSameUsername")
                .password("password")
                .role("USER")
                .build();

        when(jwtService.getUsernameFromToken("wrongTokenForTrip")).thenReturn("username");
        when(userService.findByUsername("username")).thenReturn(user);

        Trip trip = Trip.builder()
                .user(invalidUser)
                .build();

        when(tripService.findById(3L)).thenReturn(Optional.ofNullable(trip));

        assertThrows(AuthenticationFailedException.class, () -> {
            tripService.deleteTripWithIdAndToken(3L,"wrongTokenForTrip");
        });
    }


    @Test
    void deleteTripWithIdAndTokenVerifyDeleteByIdIfAdmin() {
        User user = User.builder()
                .username("username")
                .password("password")
                .role("ADMIN")
                .build();

        User invalidUser = User.builder()
                .username("notSameUsername")
                .password("password")
                .role("USER")
                .build();

        when(jwtService.getUsernameFromToken("wrongTokenForTripButAdmin")).thenReturn("username");
        when(userService.findByUsername("username")).thenReturn(user);

        Trip trip = Trip.builder()
                .user(invalidUser)
                .build();

        when(tripService.findById(3L)).thenReturn(Optional.ofNullable(trip));


        tripService.deleteTripWithIdAndToken(3L,"wrongTokenForTripButAdmin");

        verify(tripRepository).deleteById(3L);

    }

    @Test
    void deleteTripWithIdAndTokenVerifyDeleteByIdIfUsernameMatch() {
        User user = User.builder()
                .username("username")
                .password("password")
                .role("ADMIN")
                .build();


        when(jwtService.getUsernameFromToken("validToken")).thenReturn("username");
        when(userService.findByUsername("username")).thenReturn(user);

        Trip trip = Trip.builder()
                .user(user)
                .build();

        when(tripService.findById(3L)).thenReturn(Optional.ofNullable(trip));


        tripService.deleteTripWithIdAndToken(3L,"validToken");

        verify(tripRepository).deleteById(3L);

    }


}
