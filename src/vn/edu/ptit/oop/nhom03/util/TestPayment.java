package vn.edu.ptit.oop.nhom03.util;
import vn.edu.ptit.oop.nhom03.exception.InvalidPaymentException;

public class TestPayment {
    public static void checkPayment(long total, long cash) throws InvalidPaymentException {
        if (cash < total) {
            throw new InvalidPaymentException("Lỗi: Khách đưa " + cash + "đ, không đủ trả hóa đơn " + total + "đ!");
        }
        System.out.println("Thanh toán thành công! Tiền thừa: " + (cash - total));
    }

    public static void main(String[] args) {
        try {
            checkPayment(50000, 30000);
        } catch (InvalidPaymentException e) {
            System.out.println("Bắt được lỗi của Thành viên 9: " + e.getMessage());
        }
    }
}