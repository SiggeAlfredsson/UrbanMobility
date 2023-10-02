package com.siggebig.demo.service;

import org.springframework.stereotype.Service;

@Service
public class PaymentService {

    // need too mock this
    boolean validatePayment(double amount,String token) {
        return true;
    }

}
