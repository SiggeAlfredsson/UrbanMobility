package com.siggebig.demo.models;

// Should a payment be linked to a booking? or to a user and has a booking ID?


import jakarta.persistence.*;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;

// to save all payments in db, a payment is linked to a booking

@Getter
@Setter
@Entity
@Table(name = "payments")
public class Payment {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    private int amount;
    private Date date;

    @OneToOne(mappedBy = "payment")
    private Booking booking;

}
