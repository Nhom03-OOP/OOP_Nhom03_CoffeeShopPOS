package vn.edu.ptit.oop.nhom03.service;

import vn.edu.ptit.oop.nhom03.model.Order;

public class NoDiscount implements DiscountStrategy {

    @Override
    public double apply(Order order) {
        return 0.0;
    }

    @Override
    public String getStrategyName() {
        return "Không giảm giá";
    }
}
