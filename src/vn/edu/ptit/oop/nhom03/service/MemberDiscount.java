// TRUONG NGOC DIEM QUYNH - N24DCAT066
package vn.edu.ptit.oop.nhom03.service;

import vn.edu.ptit.oop.nhom03.model.Order;

public class MemberDiscount implements DiscountStrategy {

    public enum MemberLevel {
        SILVER(5),      // Giảm 5%
        GOLD(10),       // Giảm 10%
        DIAMOND(15);    // Giảm 15%

        private final double percentage;

        MemberLevel(double percentage) {
            this.percentage = percentage;
        }

        public double getPercentage() {
            return percentage;
        }
    }

    private MemberLevel level;

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
        return order.getSubtotal() * (level.getPercentage() / 100.0);
    }

    @Override
    public String getStrategyName() {
        return "Giảm giá Thành viên " + level + " (" + (int) level.getPercentage() + "%)";
    }

    @Override
    public String toString() {
        return getStrategyName();
    }
}
