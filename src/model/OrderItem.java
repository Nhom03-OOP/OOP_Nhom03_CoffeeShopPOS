package vn.edu.ptit.oop.nhom05.model;

/**
 * Một dòng trong đơn hàng: món + số lượng + ghi chú.
 * Đóng gói: các trường private, setter có kiểm tra hợp lệ.
 */
public class OrderItem {

    private final MenuItem menuItem;   // món nào (không đổi sau khi tạo)
    private final int unitPrice;       // đơn giá chốt lúc gọi món (VND)
    private int quantity;              // số lượng, luôn > 0
    private String note;               // ghi chú, ví dụ "ít đường"

    public OrderItem(MenuItem menuItem, int quantity, String note) {
        if (menuItem == null) {
            throw new IllegalArgumentException("Món không được để trống");
        }
        this.menuItem = menuItem;
        // ĐA HÌNH: getPrice() của Drink/Food/Combo tự tính giá khác nhau.
        // Chốt giá ngay lúc gọi để đơn cũ không đổi giá nếu sau này Drink bị setSize().
        this.unitPrice = menuItem.getPrice();
        setQuantity(quantity);   // dùng lại setter để cùng một luật kiểm tra
        setNote(note);
    }

    public MenuItem getMenuItem() {
        return menuItem;
    }

    public int getUnitPrice() {
        return unitPrice;
    }

    public int getQuantity() {
        return quantity;
    }

    /** Package-private: chỉ Order được sửa, để đơn đã thanh toán không bị đổi từ bên ngoài. */
    void setQuantity(int quantity) {
        if (quantity <= 0) {
            throw new IllegalArgumentException("Số lượng phải lớn hơn 0");
        }
        this.quantity = quantity;
    }

    public String getNote() {
        return note;
    }

    /** Package-private, cùng lý do với setQuantity. */
    void setNote(String note) {
        this.note = (note == null) ? "" : note.trim();
    }

    /** Thành tiền của dòng này = đơn giá đã chốt x số lượng. */
    public int getSubtotal() {
        return unitPrice * quantity;
    }

    @Override
    public String toString() {
        return menuItem.getName() + " x" + quantity
                + (note.isEmpty() ? "" : " (" + note + ")");
    }
}