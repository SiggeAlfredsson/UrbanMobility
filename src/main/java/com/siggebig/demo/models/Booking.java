package com.siggebig.demo.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "bookings")
public class Booking {

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

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;


}
