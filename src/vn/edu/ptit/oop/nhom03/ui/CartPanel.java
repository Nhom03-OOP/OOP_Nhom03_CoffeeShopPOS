package vn.edu.ptit.oop.nhom03.ui;

import vn.edu.ptit.oop.nhom03.model.Order;
import vn.edu.ptit.oop.nhom03.model.OrderItem;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.text.NumberFormat;
import java.util.Locale;

public class CartPanel extends JPanel {
    private JTable cartTable;
    private DefaultTableModel cartModel;
    private JLabel lblTotal;
    private Order currentOrder;

    public CartPanel() {
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createTitledBorder("Giỏ Hàng"));

        // 1. Cấu hình bảng Giỏ hàng
        String[] columns = {"Tên món", "SL", "Đơn giá", "Thành tiền", "Ghi chú"};
        cartModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false; 
            }
        };
        cartTable = new JTable(cartModel);
        cartTable.setRowHeight(25);
        add(new JScrollPane(cartTable), BorderLayout.CENTER);

        // 2. Khu vực hiển thị Tổng tiền và nút Thanh toán
        JPanel bottomPanel = new JPanel(new BorderLayout());
        
        lblTotal = new JLabel("Tổng cộng: 0 ₫", SwingConstants.RIGHT);
        lblTotal.setFont(new Font("Arial", Font.BOLD, 18));
        lblTotal.setForeground(Color.RED);
        lblTotal.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        bottomPanel.add(lblTotal, BorderLayout.CENTER);

        JButton btnCheckout = new JButton("Thanh Toán");
        btnCheckout.setFont(new Font("Arial", Font.BOLD, 14));
        btnCheckout.setBackground(new Color(40, 167, 69)); 
        btnCheckout.setForeground(Color.WHITE);
        btnCheckout.addActionListener(e -> openCheckoutDialog());
        bottomPanel.add(btnCheckout, BorderLayout.SOUTH);

        add(bottomPanel, BorderLayout.SOUTH);
    }

    public void setOrder(Order order) {
        this.currentOrder = order;
        refreshCart();
    }

    public void refreshCart() {
        cartModel.setRowCount(0); 
        if (currentOrder != null) {
            for (OrderItem item : currentOrder.getItems()) {
                cartModel.addRow(new Object[]{
                        item.getMenuItem().getName(),
                        item.getQuantity(),
                        formatVND((long) item.getUnitPrice()),
                        formatVND((long) item.getSubtotal()),
                        item.getNote()
                });
            }
            lblTotal.setText("Tổng cộng: " + formatVND((long) currentOrder.getSubtotal()));
        } else {
            lblTotal.setText("Tổng cộng: 0 ₫");
        }
    }

    private void openCheckoutDialog() {
        if (currentOrder == null || currentOrder.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Giỏ hàng đang trống!", "Lỗi", JOptionPane.WARNING_MESSAGE);
            return;
        }
        JFrame parentFrame = (JFrame) SwingUtilities.getWindowAncestor(this);
        CheckoutDialog dialog = new CheckoutDialog(parentFrame, currentOrder);
        dialog.setVisible(true);
        
        // Sau khi thanh toán xong, nếu đơn đã chuyển sang PAID thì làm trống giỏ hàng
        if (currentOrder.getStatus().name().equals("PAID")) {
            currentOrder = null;
            refreshCart();
        }
    }

    private String formatVND(long amount) {
        return NumberFormat.getCurrencyInstance(new Locale("vi", "VN")).format(amount);
    }
}