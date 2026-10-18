package vn.edu.ptit.oop.nhom03.ui;

import vn.edu.ptit.oop.nhom03.model.Order;
import vn.edu.ptit.oop.nhom03.model.OrderItem;
import vn.edu.ptit.oop.nhom03.model.OrderStatus; // [MERGE]: Dùng enum OrderStatus
import vn.edu.ptit.oop.nhom03.service.DiscountStrategy; // [MERGE]
import vn.edu.ptit.oop.nhom03.service.NoDiscount; // [MERGE]
import vn.edu.ptit.oop.nhom03.util.MoneyFormat; // [MERGE]: Thay formatVND cũ
import vn.edu.ptit.oop.nhom03.util.Theme; // [MERGE]

import javax.swing.*;
import javax.swing.event.ListSelectionEvent; // [MERGE]
import javax.swing.event.ListSelectionListener; // [MERGE]
import java.awt.*;
import java.awt.event.MouseAdapter; // [MERGE]
import java.awt.event.MouseEvent;   // [MERGE]
import java.util.ArrayList; // [MERGE]
import java.util.List;      // [MERGE]

/**
 * Giao diện Giỏ hàng (CartPanel) - file của Đạt được nâng cấp.
 * OOP:
 * - Đa hình & Kế thừa: Sử dụng CartTableModel kế thừa AbstractTableModel.
 * - Đóng gói: Giao tiếp lỏng (Loose coupling) với MainWindow thông qua CartListener.
 * - Đa hình: Lựa chọn DiscountStrategy trực tiếp trên combobox mà không dùng switch-case.
 */
public class CartPanel extends JPanel {
    // [MERGE]: Interface lồng định nghĩa sự kiện giao tiếp ra MainWindow (không tham chiếu ngược MainWindow)
    public interface CartListener {
        void onCartChanged(String message);
        void onOrderPaid(Order paidOrder);
    }

    private JTable cartTable;
    // [MERGE]: Thay DefaultTableModel bằng CartTableModel chuẩn kiến trúc OOP
    private CartTableModel cartModel; 
    
    // [MERGE]: Thêm 3 nhãn hiển thị Tạm tính / Giảm giá / Tổng thanh toán
    private JLabel lblSubtotal;
    private JLabel lblDiscount;
    private JLabel lblTotal;
    
    // [MERGE]: Các nút thao tác số lượng và xóa món
    private JButton btnMinus;
    private JButton btnPlus;
    private JButton btnRemove;
    private JButton btnClear;
    private JButton btnCheckout;
    
    // [MERGE]: Combobox chọn khuyến mãi trực tiếp tại giỏ
    private JComboBox<DiscountStrategy> cbxDiscount;
    private List<DiscountStrategy> discountOptions = new ArrayList<>();
    private boolean syncing = false; // [MERGE]: Cờ ngăn lặp sự kiện khi đồng bộ combobox
    
    private Order currentOrder;
    private CartListener cartListener; // [MERGE]

    public CartPanel() {
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createTitledBorder("Giỏ Hàng"));

        // 1. Cấu hình bảng Giỏ hàng [MERGE]: dùng CartTableModel
        cartModel = new CartTableModel();
        cartTable = new JTable(cartModel);
        cartTable.setRowHeight(28); // [MERGE]
        cartTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION); // [MERGE]
        
        // Căn lề và độ rộng cột [MERGE]
        if (cartTable.getColumnModel().getColumnCount() >= 7) {
            cartTable.getColumnModel().getColumn(0).setPreferredWidth(35);  // STT
            cartTable.getColumnModel().getColumn(1).setPreferredWidth(140); // Tên món
            cartTable.getColumnModel().getColumn(2).setPreferredWidth(45);  // Size
            cartTable.getColumnModel().getColumn(3).setPreferredWidth(40);  // SL
            cartTable.getColumnModel().getColumn(4).setPreferredWidth(80);  // Đơn giá
            cartTable.getColumnModel().getColumn(5).setPreferredWidth(90);  // Thành tiền
            cartTable.getColumnModel().getColumn(6).setPreferredWidth(110); // Ghi chú
        }

        // [MERGE]: Double-click dòng để xác nhận xóa nhanh
        cartTable.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2) {
                    removeSelected();
                }
            }
        });

        // [MERGE]: Lắng nghe chọn dòng để bật/tắt các nút +, -, Xóa
        cartTable.getSelectionModel().addListSelectionListener(new ListSelectionListener() {
            @Override
            public void valueChanged(ListSelectionEvent e) {
                updateButtonStates();
            }
        });

        add(new JScrollPane(cartTable), BorderLayout.CENTER);

        // 2. Khu vực điều khiển và thanh toán [MERGE]: Tinh chỉnh cấu trúc giao diện
        JPanel southContainer = new JPanel(new BorderLayout(6, 6));

        // [MERGE]: Thanh công cụ tác vụ giỏ (+, -, Xóa món, Xóa hết)
        JPanel actionToolbar = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 2));
        btnMinus = new JButton("−");
        btnMinus.setToolTipText("Giảm số lượng 1 đơn vị");
        btnPlus = new JButton("+");
        btnPlus.setToolTipText("Tăng số lượng 1 đơn vị");
        btnRemove = new JButton("Xóa món");
        btnRemove.setToolTipText("Xóa món đang chọn khỏi giỏ");
        btnClear = new JButton("Xóa hết");
        btnClear.setToolTipText("Xóa toàn bộ giỏ hàng");

        btnMinus.addActionListener(e -> modifyQuantity(-1));
        btnPlus.addActionListener(e -> modifyQuantity(1));
        btnRemove.addActionListener(e -> removeSelected());
        btnClear.addActionListener(e -> clearAll());

        actionToolbar.add(new JLabel("Số lượng:"));
        actionToolbar.add(btnMinus);
        actionToolbar.add(btnPlus);
        actionToolbar.add(Box.createHorizontalStrut(10));
        actionToolbar.add(btnRemove);
        actionToolbar.add(btnClear);
        southContainer.add(actionToolbar, BorderLayout.NORTH);

        // [MERGE]: Panel thông tin tiền và chọn khuyến mãi
        JPanel summaryPanel = new JPanel(new GridLayout(4, 2, 8, 4));
        summaryPanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(1, 0, 0, 0, Color.LIGHT_GRAY),
                BorderFactory.createEmptyBorder(6, 10, 6, 10)
        ));

        // Khuyến mãi
        summaryPanel.add(new JLabel("Khuyến mãi:"));
        cbxDiscount = new JComboBox<>();
        cbxDiscount.addItemListener(e -> {
            if (syncing || currentOrder == null) return;
            DiscountStrategy chosen = (DiscountStrategy) cbxDiscount.getSelectedItem();
            if (chosen != null) {
                currentOrder.setDiscountStrategy(chosen);
                refreshSummaryOnly();
                if (cartListener != null) {
                    cartListener.onCartChanged("Đã áp dụng khuyến mãi: " + chosen.toString());
                }
            }
        });
        summaryPanel.add(cbxDiscount);

        // Tạm tính
        summaryPanel.add(new JLabel("Tạm tính:"));
        lblSubtotal = new JLabel("0 ₫", SwingConstants.RIGHT);
        lblSubtotal.setFont(Theme.FONT_BOLD);
        summaryPanel.add(lblSubtotal);

        // Giảm giá
        summaryPanel.add(new JLabel("Giảm giá:"));
        lblDiscount = new JLabel("-0 ₫", SwingConstants.RIGHT);
        lblDiscount.setForeground(Theme.PRIMARY);
        summaryPanel.add(lblDiscount);

        // Tổng cộng
        summaryPanel.add(new JLabel("TỔNG THANH TOÁN:"));
        lblTotal = new JLabel("0 ₫", SwingConstants.RIGHT);
        lblTotal.setFont(new Font("Arial", Font.BOLD, 17));
        lblTotal.setForeground(Color.RED);
        summaryPanel.add(lblTotal);

        southContainer.add(summaryPanel, BorderLayout.CENTER);

        // Nút Thanh Toán (Giữ nguyên phong cách của Đạt)
        btnCheckout = new JButton("Thanh Toán");
        btnCheckout.setFont(new Font("Arial", Font.BOLD, 14));
        btnCheckout.setBackground(new Color(40, 167, 69)); 
        btnCheckout.setForeground(Color.WHITE);
        btnCheckout.addActionListener(e -> openCheckoutDialog());
        southContainer.add(btnCheckout, BorderLayout.SOUTH);

        add(southContainer, BorderLayout.SOUTH);

        updateButtonStates();
    }

    // [MERGE]: Đăng ký listener giao tiếp với MainWindow
    public void setCartListener(CartListener listener) {
        this.cartListener = listener;
    }

    // [MERGE]: Thiết lập danh sách khuyến mãi khả dụng
    public void setDiscountOptions(List<DiscountStrategy> options) {
        this.discountOptions = (options != null) ? new ArrayList<>(options) : new ArrayList<>();
        syncing = true;
        cbxDiscount.removeAllItems();
        for (DiscountStrategy ds : discountOptions) {
            cbxDiscount.addItem(ds);
        }
        if (currentOrder != null && currentOrder.getDiscountStrategy() != null) {
            cbxDiscount.setSelectedItem(currentOrder.getDiscountStrategy());
        }
        syncing = false;
    }

    public void setOrder(Order order) {
        this.currentOrder = order;
        cartModel.setOrder(order);
        syncDiscountComboBox();
        refreshCart();
    }

    // [MERGE]: Đồng bộ lựa chọn khuyến mãi hiển thị trên combobox
    private void syncDiscountComboBox() {
        if (currentOrder == null) return;
        syncing = true;
        DiscountStrategy currentStrategy = currentOrder.getDiscountStrategy();
        if (currentStrategy != null) {
            cbxDiscount.setSelectedItem(currentStrategy);
        }
        syncing = false;
    }

    public void refreshCart() {
        cartModel.refresh();
        refreshSummaryOnly();
        updateButtonStates();
    }

    // [MERGE]: Cập nhật các nhãn tiền tệ nhanh mà không cần vẽ lại cả bảng
    private void refreshSummaryOnly() {
        if (currentOrder != null) {
            lblSubtotal.setText(MoneyFormat.format(currentOrder.getSubtotal()));
            lblDiscount.setText("-" + MoneyFormat.format(currentOrder.getDiscountAmount()));
            lblTotal.setText(MoneyFormat.format(currentOrder.getTotal()));
        } else {
            lblSubtotal.setText("0 ₫");
            lblDiscount.setText("-0 ₫");
            lblTotal.setText("0 ₫");
        }
    }

    private void updateButtonStates() {
        int selectedRow = cartTable.getSelectedRow();
        boolean hasSelection = (selectedRow >= 0);
        boolean hasItems = (currentOrder != null && !currentOrder.isEmpty());

        btnMinus.setEnabled(hasSelection);
        btnPlus.setEnabled(hasSelection);
        btnRemove.setEnabled(hasSelection);
        btnClear.setEnabled(hasItems);
        btnCheckout.setEnabled(hasItems);
    }

    // [MERGE]: Thay đổi số lượng món được chọn (+ / -)
    public void modifyQuantity(int delta) {
        int row = cartTable.getSelectedRow();
        if (row < 0 || currentOrder == null) return;

        try {
            OrderItem item = cartModel.getItemAt(row);
            if (item == null) return;

            int newQty = item.getQuantity() + delta;
            if (newQty <= 0) {
                int confirm = JOptionPane.showConfirmDialog(this,
                        "Số lượng về 0. Bạn có muốn xóa món \"" + item.getMenuItem().getName() + "\" khỏi giỏ không?",
                        "Xác nhận xóa món", JOptionPane.YES_NO_OPTION);
                if (confirm == JOptionPane.YES_OPTION) {
                    currentOrder.removeItem(row);
                    if (cartListener != null) {
                        cartListener.onCartChanged("Đã xóa món: " + item.getMenuItem().getName());
                    }
                }
            } else if (newQty > 99) {
                JOptionPane.showMessageDialog(this,
                        "Số lượng tối đa cho mỗi món là 99!",
                        "Thông báo", JOptionPane.WARNING_MESSAGE);
                return;
            } else {
                item.setQuantity(newQty);
                if (cartListener != null) {
                    cartListener.onCartChanged(String.format("Cập nhật số lượng món \"%s\": %d",
                            item.getMenuItem().getName(), newQty));
                }
            }
            refreshCart();
            if (row < cartTable.getRowCount()) {
                cartTable.setRowSelectionInterval(row, row);
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Lỗi chỉnh sửa số lượng: " + ex.getMessage(),
                    "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
    }

    // [MERGE]: Xóa món đang chọn
    public void removeSelected() {
        int row = cartTable.getSelectedRow();
        if (row < 0 || currentOrder == null) return;

        OrderItem item = cartModel.getItemAt(row);
        if (item == null) return;

        int confirm = JOptionPane.showConfirmDialog(this,
                "Bạn có chắc muốn xóa món \"" + item.getMenuItem().getName() + "\" khỏi giỏ hàng?",
                "Xác nhận xóa", JOptionPane.YES_NO_OPTION);

        if (confirm == JOptionPane.YES_OPTION) {
            try {
                currentOrder.removeItem(row);
                refreshCart();
                if (cartListener != null) {
                    cartListener.onCartChanged("Đã xóa món: " + item.getMenuItem().getName());
                }
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Lỗi khi xóa món: " + ex.getMessage(),
                        "Lỗi", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    // [MERGE]: Xóa toàn bộ giỏ hàng
    public void clearAll() {
        if (currentOrder == null || currentOrder.isEmpty()) return;

        int confirm = JOptionPane.showConfirmDialog(this,
                "Bạn có chắc muốn làm trống toàn bộ giỏ hàng?",
                "Xác nhận xóa hết", JOptionPane.YES_NO_OPTION);

        if (confirm == JOptionPane.YES_OPTION) {
            currentOrder.clear();
            refreshCart();
            if (cartListener != null) {
                cartListener.onCartChanged("Đã làm trống giỏ hàng.");
            }
        }
    }

    // [MERGE]: Public checkout() để toolbar hoặc menu bar gọi vào
    public void checkout() {
        openCheckoutDialog();
    }

    private void openCheckoutDialog() {
        if (currentOrder == null || currentOrder.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Giỏ hàng đang trống!", "Lỗi", JOptionPane.WARNING_MESSAGE);
            return;
        }
        JFrame parentFrame = (JFrame) SwingUtilities.getWindowAncestor(this);
        
        // [MERGE]: Truyền danh sách khuyến mãi để đồng bộ đa hình
        CheckoutDialog dialog = new CheckoutDialog(parentFrame, currentOrder, discountOptions);
        dialog.setVisible(true);
        
        // [MERGE]: So sánh enum chuẩn bằng == OrderStatus.PAID
        if (currentOrder.getStatus() == OrderStatus.PAID) {
            // [MERGE]: Báo cho MainWindow lưu hóa đơn và tạo đơn mới (KHÔNG tự gán null gây mất dấu)
            if (cartListener != null) {
                cartListener.onOrderPaid(currentOrder);
            }
        } else {
            // [MERGE]: Nếu chưa thanh toán, đồng bộ lại chiến lược khuyến mãi người dùng đã đổi trong dialog
            syncDiscountComboBox();
            refreshCart();
        }
    }
}
