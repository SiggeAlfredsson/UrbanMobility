package com.siggebig.demo.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.List;


// a trip is something a supplier can make, like västtrafik can add a buss ride

@Getter
@Setter
@Entity
@Table(name = "trips")
public class Trip {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    private String departure;
    private String arrival;
    private String transportType; // ex train or bus
    private String price;
    private String travelCompany;
    private int discount; // 0-100; in %

    // Estimated / planned times
    private String departureTime;
    private String arrivalTime;

    // one trip can have many bookings, this makes it easy to add available slots left too.
    @OneToMany(mappedBy = "trip")
    private List<Booking> bookings;

}
