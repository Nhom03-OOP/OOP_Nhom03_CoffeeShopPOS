package vn.edu.ptit.oop.nhom05.service;

import vn.edu.ptit.oop.nhom05.model.Order;

public class MemberDiscount implements DiscountStrategy {
    public enum MemberLevel {
        SILVER(0.05),
        GOLD(0.10),
        DIAMOND(0.15);

        private final double rate;
        MemberLevel(double rate) { this.rate = rate; }
        public double getRate() { return rate; }
    }

    private MemberLevel level;

    public MemberDiscount(MemberLevel level) {
        this.level = level;
    }

    @Override
    public double apply(Order order) {
        if (order == null || level == null) return 0.0;
        return order.getSubtotal() * level.getRate();
    }

    @Override
    public String getStrategyName() {
        return "Giảm giá Thành viên " + level;
    }
}
