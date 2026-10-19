package vn.edu.ptit.oop.nhom03.service;

import vn.edu.ptit.oop.nhom03.model.Drink;
import vn.edu.ptit.oop.nhom03.model.Order;
import vn.edu.ptit.oop.nhom03.model.OrderItem;

import java.time.LocalTime;

public class HappyHourDiscount implements DiscountStrategy {
    private static final LocalTime START_TIME = LocalTime.of(14, 0);
    private static final LocalTime END_TIME = LocalTime.of(16, 0);

    private LocalTime customTime = null;

    // 1. Constructor mặc định không tham số -> Phục vụ CheckoutDialog.java
    public HappyHourDiscount() {
    }

    // 2. Constructor nhận tham số -> Phục vụ test case / giả lập giờ demo
    public HappyHourDiscount(LocalTime customTime) {
        this.customTime = customTime;
    }

    @Override
    public double apply(Order order) {
        if (order == null) return 0.0;

        // Ưu tiên giờ giả lập customTime, nếu không có mới lấy giờ tạo đơn
        LocalTime orderTime = customTime;
        if (orderTime == null) {
            if (order.getCreatedAt() == null) return 0.0;
            orderTime = order.getCreatedAt().toLocalTime();
        }

        if (orderTime.isBefore(START_TIME) || orderTime.isAfter(END_TIME)) {
            return 0.0;
        }

        double discount = 0.0;
        if (order.getItems() != null) {
            for (OrderItem item : order.getItems()) {
                if (item.getMenuItem() instanceof Drink) {
                    discount += item.getSubtotal() * 0.20; // Giảm 20% tổng giá đồ uống
                }
            }
        }
        return discount;
    }

    @Override
    public String getStrategyName() {
        return "Khung giờ vàng (14h-16h) - Giảm 20% đồ uống";
    }
}
