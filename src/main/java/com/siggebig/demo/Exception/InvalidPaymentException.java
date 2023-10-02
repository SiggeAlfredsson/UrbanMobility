package com.siggebig.demo.Exception;

public class InvalidPaymentException extends RuntimeException{

    public InvalidPaymentException(String message) {
        super(message);
    }
}
