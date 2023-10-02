package com.siggebig.demo.service;

import com.siggebig.demo.Exception.EntityNotFoundException;
import com.siggebig.demo.models.Booking;
import com.siggebig.demo.models.Payment;
import com.siggebig.demo.models.Trip;
import com.siggebig.demo.models.User;
import com.siggebig.demo.repository.BookingRepository;
import com.siggebig.demo.repository.PaymentRepository;
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

    @Autowired
    private BookingRepository bookingRepository;

    @Autowired
    private PaymentRepository paymentRepository;

//this was confusing as hell...
    public Booking createBookingWithTokenAndId(Long tripId, String token) {

        String username = jwtService.getUsernameFromToken(token);
        User user = userService.findByUsername(username);
        Optional<Trip> tripOptional = tripService.findById(tripId);

        if(username==null || user==null || tripOptional.isEmpty()) {
            throw new EntityNotFoundException("Entity not found");
        }

        Trip trip = tripOptional.get();

        Payment payment = Payment.builder()
                .amount(trip.getPrice()-(trip.getPrice() * ((double) trip.getDiscount() / 100)))
                .date(Calendar.getInstance().getTime())
                .build();

        Booking booking = Booking.builder()
                .user(user)
                .trip(trip)
                .payment(payment)
                .build();

        paymentRepository.save(payment);
        bookingRepository.save(booking);



        return booking;
    }




}
