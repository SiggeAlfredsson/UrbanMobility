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




    }

