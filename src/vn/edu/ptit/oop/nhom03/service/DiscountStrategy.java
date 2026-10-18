package vn.edu.ptit.oop.nhom03.service;

import vn.edu.ptit.oop.nhom03.model.Order;

public interface DiscountStrategy {
    double apply(Order order);
    String getStrategyName();
}
