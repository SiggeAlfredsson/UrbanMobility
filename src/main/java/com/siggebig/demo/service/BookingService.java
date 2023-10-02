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
        Optional<Trip> trip = tripService.findById(tripId);

        if(username==null || user==null || trip.isEmpty()) {
            throw new EntityNotFoundException("Entity not found");
        }

        Booking booking = new Booking();
        booking.setUser(user);
        trip.ifPresent(booking::setTrip); // it is always present... optional......

        Payment payment = new Payment();
        payment.setBooking(booking);
        Date date = Calendar.getInstance().getTime();
        payment.setDate(date);
        double finalAmount = trip.get().getPrice() * (1-(trip.get().getDiscount()));
        payment.setAmount(finalAmount);
        booking.setPayment(payment);

        userService.save(user);

        return booking;


    }
}
