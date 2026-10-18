package vn.edu.ptit.oop.nhom05.exception;

public class InvalidPaymentException extends Exception {
    public InvalidPaymentException(String message) {
        super(message);
    }
}