package vn.edu.ptit.oop.nhom03.model;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import vn.edu.ptit.oop.nhom03.exception.InvalidOrderException;
import vn.edu.ptit.oop.nhom03.service.DiscountStrategy;
import vn.edu.ptit.oop.nhom03.service.NoDiscount;

/** Đơn hàng. Composition: Order chứa danh sách OrderItem. */
public class Order {

    private static int counter = 0;

    private final String id;
    private int tableNo;
    private final List<OrderItem> items = new ArrayList<>();
    private OrderStatus status = OrderStatus.OPEN;
    private final LocalDateTime createdAt;
    private DiscountStrategy discountStrategy = new NoDiscount();   // mặc định, không để null

    /** Tạo đơn mới, mã tự sinh; số bàn đặt sau bằng setTableNo. */
    public Order() {
        this.id = "HD" + String.format("%03d", ++counter);
        this.createdAt = LocalDateTime.now();
    }

    /** UI gọi new Order(số bàn): mã đơn tự sinh. */
    public Order(int tableNo) {
        this();
        setTableNo(tableNo);
    }

    public Order(String id, int tableNo) {
        this(id, tableNo, LocalDateTime.now());
    }

    /** Dùng khi cần giờ cụ thể (test Happy Hour, dữ liệu mẫu cho biểu đồ). */
    public Order(String id, int tableNo, LocalDateTime createdAt) {
        if (id == null || id.trim().isEmpty()) {
            throw new InvalidOrderException("Mã đơn không được để trống");
        }
        setTableNo(tableNo);
        this.id = id;
        this.createdAt = (createdAt == null) ? LocalDateTime.now() : createdAt;
    }

    // ---------- Giỏ hàng ----------

    /** Gộp dòng nếu trùng món + size + ghi chú. */
    public void addItem(OrderItem newItem) {
        checkEditable();
        if (newItem == null) {
            throw new InvalidOrderException("Món không được để trống");
        }
        for (OrderItem it : items) {
            if (it.isSameLine(newItem)) {
                it.setQuantity(it.getQuantity() + newItem.getQuantity());
                return;
            }
        }
        items.add(newItem);
    }

    public void removeItem(OrderItem item) {
        checkEditable();
        items.remove(item);
    }

    /** Xóa dòng theo vị trí (dùng với dòng đang chọn trong JTable). */
    public void removeItem(int index) {
        checkEditable();
        items.remove(index);
    }

    /** Đổi số lượng dòng thứ index; CartPanel nên gọi hàm này thay vì sửa OrderItem. */
    public void updateQuantity(int index, int quantity) {
        checkEditable();
        items.get(index).setQuantity(quantity);
    }

    public void clear() {
        checkEditable();
        items.clear();
    }

    private void checkEditable() {
        if (status != OrderStatus.OPEN) {
            throw new InvalidOrderException(
                    "Đơn đang ở trạng thái \"" + status.getLabel() + "\", không thể sửa");
        }
    }

    // ---------- Tính tiền ----------

    public double getSubtotal() {
        double sum = 0;
        for (OrderItem it : items) sum += it.getSubtotal();
        return sum;
    }

    /** Số tiền được giảm (DiscountStrategy.apply trả số tiền giảm). */
    public double getDiscountAmount() {
        return discountStrategy.apply(this);
    }

    public double getTotal() {
        return getSubtotal() - discountStrategy.apply(this);
    }

    // ---------- Trạng thái ----------

    public void markPaid() {
        checkEditable();
        if (items.isEmpty()) {
            throw new InvalidOrderException("Giỏ hàng trống, không thể thanh toán");
        }
        status = OrderStatus.PAID;
        lockItems();
    }

    public void cancel() {
        checkEditable();
        status = OrderStatus.CANCELLED;
        lockItems();
    }

    private void lockItems() {
        for (OrderItem it : items) it.lock();
    }

    // ---------- Getter / setter ----------

    public String getId()                { return id; }
    public int getTableNo()              { return tableNo; }
    public OrderStatus getStatus()       { return status; }
    public LocalDateTime getCreatedAt()  { return createdAt; }
    public boolean isEmpty()             { return items.isEmpty(); }

    /** Giờ tạo đơn dạng chữ để hiện lên UI, ví dụ 19/10/2026 14:05. */
    public String getFormattedCreatedAt() {
        return createdAt.format(java.time.format.DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm"));
    }

    public void setTableNo(int tableNo) {
        if (tableNo <= 0) {
            throw new InvalidOrderException("Số bàn phải lớn hơn 0");
        }
        this.tableNo = tableNo;
    }

    public List<OrderItem> getItems() {
        return Collections.unmodifiableList(items);
    }

    public DiscountStrategy getDiscountStrategy() { return discountStrategy; }

    public void setDiscountStrategy(DiscountStrategy s) {
        this.discountStrategy = (s == null) ? new NoDiscount() : s;
    }
}