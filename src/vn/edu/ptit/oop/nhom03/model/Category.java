package vn.edu.ptit.oop.nhom03.model;

/** TẠM THỜI: chỉ để MenuRepository build được. Nếu chủ MenuItem đã có Category thì xóa file này. */
public enum Category {
    DRINK("Nước uống"),
    FOOD("Đồ ăn"),
    COMBO("Combo");

    private final String label;

    Category(String label) { this.label = label; }

    public String getLabel() { return label; }
}
