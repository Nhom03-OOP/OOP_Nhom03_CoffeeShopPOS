package vn.edu.ptit.oop.nhom03.service;

import vn.edu.ptit.oop.nhom03.model.Order;

public class MemberDiscount implements DiscountStrategy {
    public enum MemberLevel {
        SILVER(5),
        GOLD(10),
        DIAMOND(15);

        private final double rate;
        MemberLevel(double rate) { this.rate = rate; }
        public double getRate() { return rate; }
    }

    private MemberLevel level;

    // Constructor mặc định khớp với cách gọi new MemberDiscount() ở CheckoutDialog
    public MemberDiscount() {
        this.level = MemberLevel.SILVER;
    }

    public MemberDiscount(MemberLevel level) {
        this.level = level;
    }

    public MemberLevel getLevel() {
        return level;
    }

    public void setLevel(MemberLevel level) {
        this.level = level;
    }

    @Override
    public double apply(Order order) {
        if (order == null || level == null) return 0.0;
        return order.getSubtotal() * (level.getRate() / 100.0);
    }

    @Override
    public String getStrategyName() {
        return "Giảm giá Thành viên " + level;
    }
    @Override
    public String toString() {
        return getStrategyName();
    }
}
