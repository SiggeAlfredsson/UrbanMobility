package com.siggebig.demo.service;

import com.siggebig.demo.Exception.AuthenticationFailedException;
import com.siggebig.demo.Exception.EntityNotFoundException;
import com.siggebig.demo.models.Trip;
import com.siggebig.demo.models.User;
import com.siggebig.demo.repository.TripRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class TripService {

    @Autowired(required = false)
    private TripRepository tripRepository;

    @Autowired
    private JwtService jwtService;

    @Autowired
    private UserService userService;


    public Optional<Trip> findById(long tripId) {
        return tripRepository.findById(tripId);
    }

    public List<Trip> getAllTrips() {
        return tripRepository.findAll();
    }

    public void deleteWithId(long tripId) {
        tripRepository.deleteById(tripId);
    }

    public void deleteTripWithIdAndToken(long tripId, String token) {


        String username = jwtService.getUsernameFromToken(token);
        User user = userService.findByUsername(username);

        if(user==null || username==null) {
            throw new AuthenticationFailedException("No user found from token"); //auth failed or entitynotfound?
        }


        Optional<Trip> trip = findById(tripId);

        if(trip.isEmpty()){
            throw new EntityNotFoundException("No trip found with that id");
        }

        //check so token is from trip owner or a admin user
        if (trip.get().getUser().getUsername().equals(username) || user.getRole().equals("ADMIN")) {
            deleteWithId(tripId);
        }
        else {
            throw new AuthenticationFailedException("Token is not from trip owner");
        }
    }

    }

