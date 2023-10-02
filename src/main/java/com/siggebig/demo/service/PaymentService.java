package com.siggebig.demo.service;

import com.siggebig.demo.models.Booking;
import com.siggebig.demo.models.Payment;
import org.springframework.stereotype.Service;

@Service
public class PaymentService {

    // need too mock this
    boolean validatePayment(Payment payment) {

        return true;
    }

}
