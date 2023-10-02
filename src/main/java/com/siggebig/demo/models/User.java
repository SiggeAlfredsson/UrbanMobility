package com.siggebig.demo.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import jdk.jfr.DataAmount;
import lombok.*;

import java.util.List;

 // a user can be both a user and a supplier, depends on the ROLE
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "users")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @NotBlank
    private String username;


//Can i have this only in dto?
    @NotBlank
//    @Size(min=8)
    private String password;

    @Email
    private String email;

    private String phoneNumber;
    private String paymentMethod; // ?? swish
    private int paymentNumber; // swish number

     //@Enumerated(EnumType.STRING) never got it to work with enums
    private String role;


    // should a list of all payments be here to?


    @OneToMany(mappedBy = "user")
    @JsonIgnoreProperties("user")
    private List<Booking> bookings;

    @OneToMany(mappedBy = "user")
    @JsonIgnoreProperties("user")
    private List<Trip> trips;

}
