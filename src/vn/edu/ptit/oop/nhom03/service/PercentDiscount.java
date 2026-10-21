package vn.edu.ptit.oop.nhom03.service;

import vn.edu.ptit.oop.nhom03.model.Order;

public class PercentDiscount implements DiscountStrategy {

    private double percentage; 

    public PercentDiscount() {
        this.percentage = 10.0; 
    }

    public PercentDiscount(double percentage) {
        
        if (percentage > 0 && percentage <= 1.0) {
            this.percentage = percentage * 100.0;
        } else {
            this.percentage = percentage;
        }
    }

    public double getPercentage() {
        return percentage;
    }

    public void setPercentage(double percentage) {
        if (percentage > 0 && percentage <= 1.0) {
            this.percentage = percentage * 100.0;
        } else {
            this.percentage = percentage;
        }
    }

    @Override
    public double apply(Order order) {
        if (order == null) return 0.0;
        return order.getSubtotal() * (percentage / 100.0);
    }

    @Override
    public String getStrategyName() {
        return String.format("Giảm giá %,.0f%%", percentage);
    }

    @Override
    public String toString() {
        return getStrategyName();
    }
}
