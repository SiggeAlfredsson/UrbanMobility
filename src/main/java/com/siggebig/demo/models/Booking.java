package com.siggebig.demo.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

// A user can make a booking, to go from A - B , but only a supplier should be able to add a trip

@Getter
@Setter
@Entity
@Table(name = "bookings")
public class Booking {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;



    // the user need to make one payment when booking a trip
    @OneToOne
    @JoinColumn(name = "payment_id")
    private Payment payment;

    // the booking must have one user, but the user can have many bookings
    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    // the booking must have a trip, what else are you paying for?? each booking is individual so 1to1
    @OneToOne
    @JoinColumn(name = "trip_id")
    private Trip trip;


}
