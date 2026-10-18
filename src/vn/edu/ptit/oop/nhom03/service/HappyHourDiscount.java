package vn.edu.ptit.oop.nhom03.service;

import vn.edu.ptit.oop.nhom03.model.Drink;
import vn.edu.ptit.oop.nhom03.model.Order;
import vn.edu.ptit.oop.nhom03.model.OrderItem;

import java.time.LocalTime;

public class HappyHourDiscount implements DiscountStrategy {
    private static final LocalTime START_TIME = LocalTime.of(14, 0);
    private static final LocalTime END_TIME = LocalTime.of(16, 0);

    @Override
    public double apply(Order order) {
        if (order == null || order.getCreatedAt() == null) return 0.0;

        LocalTime orderTime = order.getCreatedAt().toLocalTime();
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
