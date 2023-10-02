package com.siggebig.demo.models;

// Should a payment be linked to a booking? or to a user and has a booking ID?


import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.*;

import java.util.Date;

// to save all payments in db, a payment is linked to a booking

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "payments")
public class Payment {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    private double amount;
    private Date date;

    @OneToOne
    @JoinColumn(name = "booking_id")
    @JsonIgnoreProperties("payment")
    private Booking booking;

}
