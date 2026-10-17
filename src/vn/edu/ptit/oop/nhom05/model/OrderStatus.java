package vn.edu.ptit.oop.nhom05.model;

/** Trạng thái của một đơn hàng. */
public enum OrderStatus {
    NEW("Đang gọi món"),
    PAID("Đã thanh toán"),
    CANCELLED("Đã hủy");

    private final String label;

    OrderStatus(String label) {
        this.label = label;
    }

    public String getLabel() {
        return label;
    }
}
