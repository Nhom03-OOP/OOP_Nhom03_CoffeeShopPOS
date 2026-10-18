package vn.edu.ptit.oop.nhom03.exception;

public class InvalidPaymentException extends Exception {
    public InvalidPaymentException(String message) {
        super(message);
    }
}