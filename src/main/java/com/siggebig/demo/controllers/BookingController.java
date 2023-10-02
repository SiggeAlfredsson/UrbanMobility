package com.siggebig.demo.controllers;


import com.siggebig.demo.Exception.AuthenticationFailedException;
import com.siggebig.demo.Exception.EntityNotFoundException;
import com.siggebig.demo.models.Booking;
import com.siggebig.demo.service.BookingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("trip/book")
public class BookingController {

    @Autowired
    private BookingService bookingService;


    //add booking
    @PostMapping("/{id}")
    public ResponseEntity<Booking> createBooking (@PathVariable("id")Long tripId, @RequestHeader("JWTToken")String token) {

        try {
            Booking booking = bookingService.createBookingWithTokenAndId(tripId,token);
            return ResponseEntity.ok(booking);
        } catch (EntityNotFoundException e){
            return ResponseEntity.badRequest().header("x-info", "Invalid data, trip id or token").build(); //would be nice to have one for each
        }
    }

    //remove booking
    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteBooking (@PathVariable("id")long bookingId, @RequestHeader("JWTToken")String token) {
        try {
            bookingService.deleteBookingByIdAndToken(bookingId,token);
        } catch (EntityNotFoundException e) {
            return ResponseEntity.status(404).body("No booking with that id");
        } catch (AuthenticationFailedException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid Token");
        }
        return ResponseEntity.ok().body("Booking deleted");
    }

}
