package com.siggebig.demo.controllers;


import com.siggebig.demo.Exception.AuthenticationFailedException;
import com.siggebig.demo.Exception.EntityNotFoundException;
import com.siggebig.demo.models.Trip;
import com.siggebig.demo.service.TripService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/trip")
public class TripController {

    @Autowired
    private TripService tripService;

    // create a trip with token, need to be supplier
    @PostMapping()
    public ResponseEntity<Trip> addTrip(@RequestBody Trip trip,@RequestHeader ("JWTToken") String token) {
        try{
            tripService.createTripWithToken(trip, token);
            return ResponseEntity.ok(trip);
        } catch (AuthenticationFailedException e) {
            return ResponseEntity.badRequest().header("x-info", "Invalid token").build();        }
    }

    // get all trips info
    @GetMapping()
    public ResponseEntity<List<Trip>> getAllTrips() {
        List<Trip> trips = tripService.getAllTrips();

        if(trips.isEmpty()) {
            return ResponseEntity
                    .status(204)
                    .header("x-info", "No trips found in db")
                    .build();
        } else {
            return ResponseEntity.ok(trips);
        }
    }

    // get trip from id
    @GetMapping("/{id}")
    public ResponseEntity<Optional<Trip>> getTripById (@PathVariable("id") long tripId) {
        Optional<Trip> trip = tripService.findById(tripId);
        if (trip.isEmpty()) {
            return ResponseEntity.status(204).header("x-info", "No trip with that id").build();
        } else {
            return ResponseEntity.ok(trip);
        }
    }


    // delete a trip with id , check token that role is supplier and username match the creator of the trip, unless role is admin
    @DeleteMapping("/delete/{id}")
    public ResponseEntity<String> deleteTripWithIdAndToken(@PathVariable("id")long tripId,@RequestHeader("JWTToken") String token) {
        try {
            tripService.deleteTripWithIdAndToken(tripId,token);
        } catch (EntityNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Trip not found");
        } catch (AuthenticationFailedException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid token");
        }

        return ResponseEntity.ok("Trip deleted successfully");
    }



    // edit a trip with id , check token match supplier
    @PutMapping("/update")
    public ResponseEntity<Trip> updateTripWithToken(@RequestBody Trip trip,@RequestHeader("JWTToken")String token) {

        try {
            tripService.updateTripWithToken(trip,token);
        } catch (AuthenticationFailedException e) {
            return ResponseEntity.status(400).header("x-info", "Auth failed").build();
        }

        return ResponseEntity.ok(trip);

    }

}
