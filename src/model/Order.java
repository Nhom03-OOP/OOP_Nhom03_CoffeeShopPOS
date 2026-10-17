package vn.edu.ptit.oop.nhom05.model;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import vn.edu.ptit.oop.nhom05.service.DiscountStrategy; // chỉnh package theo bạn Diễm Quỳnh

/**
 * Đơn hàng. KẾT HỢP (Composition): Order chứa danh sách OrderItem.
 */
public class Order {

    private final String id;
    private final int tableNo;
    private final List<OrderItem> items = new ArrayList<>();
    private OrderStatus status;
    private final LocalDateTime createdAt;
    private DiscountStrategy discountStrategy;   // null = không giảm giá

    public Order(String id, int tableNo) {
        this(id, tableNo, LocalDateTime.now());
    }

    /** Dùng khi cần giờ cụ thể: test Happy Hour 14h-16h, tạo dữ liệu mẫu cho biểu đồ theo giờ. */
    public Order(String id, int tableNo, LocalDateTime createdAt) {
        if (id == null || id.trim().isEmpty()) {
            throw new IllegalArgumentException("Mã đơn không được để trống");
        }
        if (tableNo <= 0) {
            throw new IllegalArgumentException("Số bàn phải lớn hơn 0");
        }
        this.id = id;
        this.tableNo = tableNo;
        this.status = OrderStatus.NEW;
        this.createdAt = (createdAt == null) ? LocalDateTime.now() : createdAt;
    }

    // ---------- Thao tác giỏ hàng ----------

    /**
     * Thêm món. Nếu đã có cùng mã, cùng giá, cùng ghi chú thì cộng dồn số lượng.
     * (Phải so cả giá vì cùng một Drink nhưng khác size thì cùng id mà khác giá.)
     */
    public void addItem(MenuItem menuItem, int quantity, String note) {
        checkEditable();
        if (menuItem == null) {
            throw new IllegalArgumentException("Món không được để trống");
        }
        if (quantity <= 0) {
            throw new IllegalArgumentException("Số lượng phải lớn hơn 0");
        }
        String newNote = (note == null) ? "" : note.trim();
        for (OrderItem it : items) {
            if (it.getMenuItem().getId().equals(menuItem.getId())
                    && it.getUnitPrice() == menuItem.getPrice()
                    && it.getNote().equals(newNote)) {
                it.setQuantity(it.getQuantity() + quantity);
                return;
            }
        }
        items.add(new OrderItem(menuItem, quantity, newNote));
    }

    /** Xóa dòng theo vị trí (dùng với dòng đang chọn trong JTable). */
    public void removeItem(int index) {
        checkEditable();
        items.remove(index);
    }

    /** Đổi số lượng của dòng thứ index (giao diện gọi hàm này thay vì sửa OrderItem trực tiếp). */
    public void updateQuantity(int index, int quantity) {
        checkEditable();
        items.get(index).setQuantity(quantity);
    }

    public void clearItems() {
        checkEditable();
        items.clear();
    }

    /** Chỉ cho sửa khi đơn còn ở trạng thái NEW. */
    private void checkEditable() {
        if (status != OrderStatus.NEW) {
            throw new IllegalStateException("Đơn đang ở trạng thái \"" + status.getLabel() + "\", không thể sửa");
        }
    }

    // ---------- Tính tiền ----------

    /** Tổng tiền hàng trước khuyến mãi. */
    public double getSubtotal() {
        double sum = 0;
        for (OrderItem it : items) {
            sum += it.getSubtotal();
        }
        return sum;
    }

    /** Số tiền được giảm (do DiscountStrategy tính). */
    public double getDiscountAmount() {
        return (discountStrategy == null) ? 0 : discountStrategy.apply(this);
    }

    /** Tổng tiền khách phải trả = tiền hàng - giảm giá (không âm). */
    public double getTotal() {
        return Math.max(0, getSubtotal() - getDiscountAmount());
    }

    // ---------- Trạng thái ----------

    /** Đánh dấu đã thanh toán. Không cho thanh toán giỏ hàng rỗng. */
    public void markPaid() {
        if (items.isEmpty()) {
            throw new IllegalStateException("Giỏ hàng trống, không thể thanh toán");
        }
        checkEditable();
        status = OrderStatus.PAID;
    }

    public void cancel() {
        checkEditable();
        status = OrderStatus.CANCELLED;
    }

    // ---------- Getter / setter ----------

    public String getId() { return id; }

    public int getTableNo() { return tableNo; }

    public OrderStatus getStatus() { return status; }

    public LocalDateTime getCreatedAt() { return createdAt; }

    public boolean isEmpty() { return items.isEmpty(); }

    /** ĐÓNG GÓI: trả về bản chỉ-đọc, bên ngoài không sửa trực tiếp list nội bộ. */
    public List<OrderItem> getItems() {
        return Collections.unmodifiableList(items);
    }

    public DiscountStrategy getDiscountStrategy() { return discountStrategy; }

    public void setDiscountStrategy(DiscountStrategy discountStrategy) {
        this.discountStrategy = discountStrategy;
    }
}