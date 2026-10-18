package vn.edu.ptit.oop.nhom03.service;

import vn.edu.ptit.oop.nhom03.model.Order;

public class PercentDiscount implements DiscountStrategy {
    private double percentage;

    public PercentDiscount(double percentage) {
        this.percentage = percentage;
    }

    public double getPercentage() {
        return percentage;
    }

    public void setPercentage(double percentage) {
        this.percentage = percentage;
    }

    @Override
    public double apply(Order order) {
        if (order == null) return 0.0;
        return order.getSubtotal() * (percentage / 100.0);
    }

    @Override
    public String getStrategyName() {
        return "Giảm " + percentage + "%";
    }
}
