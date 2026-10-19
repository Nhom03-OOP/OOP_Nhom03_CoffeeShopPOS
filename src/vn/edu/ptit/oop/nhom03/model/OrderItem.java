package vn.edu.ptit.oop.nhom03.model;

import vn.edu.ptit.oop.nhom03.exception.InvalidOrderException;

/** Một dòng trong đơn hàng: món + số lượng + ghi chú. */
public class OrderItem {

    private final MenuItem menuItem;
    private final int unitPrice;     // đơn giá chốt lúc gọi món
    private int quantity;            // 1..99
    private final String note;
    private boolean locked;          // Order khóa khi đơn đã PAID/CANCELLED

    public OrderItem(MenuItem menuItem, int quantity, String note) {
        if (menuItem == null) {
            throw new InvalidOrderException("Món không được để trống");
        }
        this.menuItem = menuItem;
        this.unitPrice = menuItem.getPrice();   // đa hình
        setQuantity(quantity);
        this.note = (note == null) ? "" : note.trim();
    }

    public MenuItem getMenuItem() { return menuItem; }
    public int getUnitPrice()     { return unitPrice; }
    public int getQuantity()      { return quantity; }
    public String getNote()       { return note; }
    public int getSubtotal()      { return unitPrice * quantity; }

    /** UI gọi trực tiếp nên public; đơn đã khóa thì không cho sửa. */
    public void setQuantity(int quantity) {
        if (locked) {
            throw new InvalidOrderException("Đơn đã đóng, không thể sửa số lượng");
        }
        if (quantity < 1 || quantity > 99) {
            throw new InvalidOrderException("Số lượng phải từ 1 đến 99");
        }
        this.quantity = quantity;
    }

    /** Chỉ Order được khóa. */
    void lock() { this.locked = true; }

    /** Cùng món + cùng size + cùng ghi chú thì được gộp dòng. */
    boolean isSameLine(OrderItem o) {
        return menuItem.getCode().equals(o.menuItem.getCode())
                && menuItem.getSizeLabel().equals(o.menuItem.getSizeLabel())
                && note.equals(o.note);
    }

    @Override
    public String toString() {
        return menuItem.getName() + " x" + quantity
                + (note.isEmpty() ? "" : " (" + note + ")");
    }
}