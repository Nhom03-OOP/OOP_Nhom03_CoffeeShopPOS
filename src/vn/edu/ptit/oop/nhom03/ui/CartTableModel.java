package vn.edu.ptit.oop.nhom05.ui;

import vn.edu.ptit.oop.nhom05.model.Order;
import vn.edu.ptit.oop.nhom05.model.OrderItem;
import vn.edu.ptit.oop.nhom05.util.MoneyFormat;

import javax.swing.table.AbstractTableModel;
import java.util.List;

/**
 * Model bảng hiển thị danh sách món trong giỏ hàng (CartTableModel).
 * OOP:
 * - Kế thừa: Kế thừa AbstractTableModel của Swing.
 * - Đóng gói: Quản lý dữ liệu giỏ hàng thông qua đối tượng Order, không để lộ list nội bộ.
 * - Đa hình: Lấy thông tin kích cỡ bằng getMenuItem().getSizeLabel() thay vì dùng instanceof.
 */
public class CartTableModel extends AbstractTableModel {
    private static final String[] COLUMN_NAMES = {
            "STT", "Tên món", "Size", "SL", "Đơn giá", "Thành tiền", "Ghi chú"
    };

    private static final Class<?>[] COLUMN_CLASSES = {
            Integer.class, String.class, String.class, Integer.class, String.class, String.class, String.class
    };

    private Order order;

    public CartTableModel() {
        this.order = null;
    }

    public CartTableModel(Order order) {
        this.order = order;
    }

    public void setOrder(Order order) {
        this.order = order;
        fireTableDataChanged();
    }

    public void refresh() {
        fireTableDataChanged();
    }

    public OrderItem getItemAt(int rowIndex) {
        if (order == null) return null;
        List<OrderItem> items = order.getItems();
        if (rowIndex < 0 || rowIndex >= items.size()) {
            return null;
        }
        return items.get(rowIndex);
    }

    @Override
    public int getRowCount() {
        if (order == null) return 0;
        return order.getItems().size();
    }

    @Override
    public int getColumnCount() {
        return COLUMN_NAMES.length;
    }

    @Override
    public String getColumnName(int column) {
        return COLUMN_NAMES[column];
    }

    @Override
    public Class<?> getColumnClass(int columnIndex) {
        return COLUMN_CLASSES[columnIndex];
    }

    @Override
    public boolean isCellEditable(int rowIndex, int columnIndex) {
        // Toàn bộ ô chỉ đọc, thao tác qua nút bấm hoặc dialog
        return false;
    }

    @Override
    public Object getValueAt(int rowIndex, int columnIndex) {
        OrderItem item = getItemAt(rowIndex);
        if (item == null) return "";

        switch (columnIndex) {
            case 0: // STT
                return rowIndex + 1;
            case 1: // Tên món
                return item.getMenuItem().getName();
            case 2: // Size (OOP: Đa hình qua getSizeLabel(), không dùng instanceof)
                String sizeLabel = item.getMenuItem().getSizeLabel();
                return (sizeLabel != null && !sizeLabel.isEmpty()) ? sizeLabel : "-";
            case 3: // Số lượng
                return item.getQuantity();
            case 4: // Đơn giá
                return MoneyFormat.format(item.getUnitPrice());
            case 5: // Thành tiền
                return MoneyFormat.format(item.getSubtotal());
            case 6: // Ghi chú
                String note = item.getNote();
                return (note != null) ? note : "";
            default:
                return "";
        }
    }
}
