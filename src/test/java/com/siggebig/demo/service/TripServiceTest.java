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


}
