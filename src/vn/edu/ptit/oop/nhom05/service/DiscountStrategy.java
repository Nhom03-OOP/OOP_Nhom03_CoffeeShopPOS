package vn.edu.ptit.oop.nhom05.service;

import vn.edu.ptit.oop.nhom05.model.Order;

public interface DiscountStrategy {
    double apply(Order order);
    String getStrategyName();
}
