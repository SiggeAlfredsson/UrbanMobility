package com.siggebig.demo.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;


// a trip is something a supplier can make, like västtrafik can add a buss ride

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "trips")
public class Trip {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    private String departure;
    private String arrival;
    private String transportType; // ex train or bus
    private int price;
    //private String travelCompany; added a user instead, that is the company
    private int discount; // 0-100; in %

    // Estimated / planned times . Correct like this or use localdatetime?
    private LocalDate departureDate;
    private LocalDate arrivalDate;
    private LocalTime departureTime;
    private LocalTime arrivalTime;

    // one trip can have many bookings, this makes it easy to add available slots left too.
    @OneToMany(mappedBy = "trip")
    @JsonIgnoreProperties("trip")
    private List<Booking> bookings;

    public void addBooking(Booking booking) {
        if (bookings == null) {
            bookings = new ArrayList<>();
        }
        bookings.add(booking);
    }

    @ManyToOne
    @JoinColumn(name = "user_id") //A trip is linked to a supplier(ex västtrafik)
    private User user;



}
