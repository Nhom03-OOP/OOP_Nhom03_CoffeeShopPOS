package vn.edu.ptit.oop.nhom03.service;

import vn.edu.ptit.oop.nhom03.exception.InvalidPaymentException;
import vn.edu.ptit.oop.nhom03.model.Order;

/** Xu ly nghiep vu thanh toan, tach khoi giao dien Swing. */
public class PaymentService {
    public void pay(Order order, long customerCash) throws InvalidPaymentException {
        if (order == null) throw new InvalidPaymentException("Don hang khong ton tai.");
        if (order.isEmpty()) throw new InvalidPaymentException("Gio hang dang trong.");
        if (customerCash < 0) throw new InvalidPaymentException("Tien khach dua khong hop le.");

        long total = Math.round(order.getTotal());
        if (customerCash < total) {
            throw new InvalidPaymentException("Khach dua thieu " + (total - customerCash) + " VND.");
        }
        try {
            order.markPaid();
        } catch (RuntimeException ex) {
            throw new InvalidPaymentException("Khong the thanh toan don hang: " + ex.getMessage());
        }
    }
}
