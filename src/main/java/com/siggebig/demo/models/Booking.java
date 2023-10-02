package com.siggebig.demo.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.*;

// A user can make a booking, to go from A - B , but only a supplier should be able to add a trip

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "bookings")
public class Booking {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;



    // the user need to make one payment when booking a trip

    @OneToOne(mappedBy = "booking")
    @JsonIgnoreProperties("booking")
    private Payment payment;

    // the booking must have one user, but the user can have many bookings
    @ManyToOne
    @JoinColumn(name = "user_id")
    @JsonIgnoreProperties("user")
    private User user;

    // the booking must have a trip, what else are you paying for?? each booking is individual
    // but a trip can have many individual bookings so manytoone
    @ManyToOne
    @JoinColumn(name = "trip_id")
    @JsonIgnoreProperties("trip")
    private Trip trip;


}
