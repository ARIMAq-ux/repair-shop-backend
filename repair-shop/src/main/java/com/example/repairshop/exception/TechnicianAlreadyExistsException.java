package com.example.repairshop.exception;

public class TechnicianAlreadyExistsException extends RuntimeException {
    public TechnicianAlreadyExistsException(String message) {
        super(message);
    }
}