package com.bryan.dompetku.exception;

public class InvalidCredentialsException extends RuntimeException {

    public InvalidCredentialsException() {
        super("Email atau password salah");
    }
}
