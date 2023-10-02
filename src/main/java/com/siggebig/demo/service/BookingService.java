package com.siggebig.demo.service;

import com.siggebig.demo.Exception.EntityNotFoundException;
import com.siggebig.demo.models.Booking;
import com.siggebig.demo.models.Payment;
import com.siggebig.demo.models.Trip;
import com.siggebig.demo.models.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Calendar;
import java.util.Date;
import java.util.Optional;

@Service
public class BookingService {

    @Autowired
    private JwtService jwtService;

    @Autowired
    private UserService userService;

    @Autowired
    private TripService tripService;

    public Booking createBookingWithTokenAndId(Long tripId, String token) {

        String username = jwtService.getUsernameFromToken(token);
        User user = userService.findByUsername(username);
        Optional<Trip> tripOptional = tripService.findById(tripId);

        if(username==null || user==null || tripOptional.isEmpty()) {
            throw new EntityNotFoundException("Entity not found");
        }

        Trip trip = tripOptional.get();

        //way to much logic here

        Booking booking = new Booking();
        booking.setTrip(trip);
        Payment payment = new Payment();
        Date date = Calendar.getInstance().getTime();
        payment.setDate(date);
        double finalAmount = trip.getPrice() * (1-(trip.getDiscount()));
        payment.setAmount(finalAmount);

        payment.setBooking(booking);
        booking.setPayment(payment);
        booking.setUser(user);
        userService.save(user);


        trip.addBooking(booking);
        tripService.save(trip);



        return booking;


    }
}
