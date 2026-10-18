package vn.edu.ptit.oop.nhom03.ui;

import vn.edu.ptit.oop.nhom03.model.Order;
import vn.edu.ptit.oop.nhom03.model.OrderItem;
import vn.edu.ptit.oop.nhom03.service.*;
import vn.edu.ptit.oop.nhom03.util.MoneyFormat; // [MERGE]: Dùng MoneyFormat chuẩn ISO và JDK 17+

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import java.awt.*;
import java.util.Arrays; // [MERGE]
import java.util.List;   // [MERGE]

/**
 * Hộp thoại thanh toán hóa đơn (CheckoutDialog) - file của Đạt được nâng cấp.
 * OOP:
 * - Đa hình: JComboBox<DiscountStrategy> gọi trực tiếp strategy.apply(order), loại bỏ switch-case.
 * - Đóng gói: Quản lý biến trạng thái (cashValid, finalAmount) thay vì kiểm tra text nhãn.
 */
public class CheckoutDialog extends JDialog {
    // [MERGE]: Đổi JComboBox<String> sang JComboBox<DiscountStrategy> (đa hình, không switch)
    private JComboBox<DiscountStrategy> cbxPromo; 
    private JTextField txtCustomerCash;
    private JLabel lblTotalAmount, lblFinalAmount, lblChange;
    private Order currentOrder;

    // [MERGE]: Thêm biến trạng thái để xác thực tiền khách đưa
    private long finalAmount = 0;   // [MERGE]
    private boolean cashValid = false; // [MERGE]

    // [MERGE]: Giữ nguyên constructor 2 tham số của Đạt để tương thích
    public CheckoutDialog(JFrame parent, Order order) {
        this(parent, order, Arrays.asList(
                new NoDiscount(),
                new PercentDiscount(0.1),
                new MemberDiscount(),
                new HappyHourDiscount()
        )); // [MERGE]
    }

    // [MERGE]: Thêm constructor 3 tham số để đồng bộ danh sách khuyến mãi từ MainWindow
    public CheckoutDialog(JFrame parent, Order order, List<DiscountStrategy> discountOptions) {
        super(parent, "Thanh Toán Đơn Hàng - Bàn " + order.getTableNo(), true);
        this.currentOrder = order;

        setSize(440, 340); // [MERGE]: Mở rộng kích thước cho thoáng
        setLocationRelativeTo(parent);
        setLayout(new BorderLayout(10, 10));

        // --- PANEL THÔNG TIN THANH TOÁN ---
        JPanel infoPanel = new JPanel(new GridLayout(5, 2, 10, 15));
        infoPanel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        infoPanel.add(new JLabel("Tổng tiền hàng:"));
        lblTotalAmount = new JLabel(MoneyFormat.format(order.getSubtotal())); // [MERGE]
        infoPanel.add(lblTotalAmount);

        infoPanel.add(new JLabel("Áp dụng KM:"));
        // [MERGE]: Khởi tạo combobox bằng danh sách đối tượng DiscountStrategy đa hình
        cbxPromo = new JComboBox<>(discountOptions.toArray(new DiscountStrategy[0]));
        if (order.getDiscountStrategy() != null) {
            cbxPromo.setSelectedItem(order.getDiscountStrategy()); // [MERGE]: Đồng bộ lựa chọn từ CartPanel
        }
        infoPanel.add(cbxPromo);

        infoPanel.add(new JLabel("Thành tiền:"));
        lblFinalAmount = new JLabel(MoneyFormat.format(order.getTotal())); // [MERGE]
        lblFinalAmount.setFont(new Font("Arial", Font.BOLD, 14));
        lblFinalAmount.setForeground(Color.RED);
        infoPanel.add(lblFinalAmount);

        infoPanel.add(new JLabel("Tiền khách đưa:"));
        txtCustomerCash = new JTextField();
        infoPanel.add(txtCustomerCash);

        infoPanel.add(new JLabel("Tiền thừa:"));
        lblChange = new JLabel("0 ₫");
        lblChange.setFont(new Font("Arial", Font.BOLD, 14));
        lblChange.setForeground(Color.BLUE);
        infoPanel.add(lblChange);

        add(infoPanel, BorderLayout.CENTER);

        // --- PANEL NÚT BẤM ---
        JPanel btnPanel = new JPanel();
        JButton btnPayAndPrint = new JButton("Xác nhận & In Hóa Đơn");
        btnPayAndPrint.setFont(new Font("Arial", Font.BOLD, 14));
        btnPayAndPrint.setBackground(new Color(40, 167, 69)); // [MERGE]: Giữ nút xanh lá của Đạt
        btnPayAndPrint.setForeground(Color.WHITE); // [MERGE]
        
        btnPanel.add(btnPayAndPrint);
        add(btnPanel, BorderLayout.SOUTH);

        // --- GẮN SỰ KIỆN LẮNG NGHE ---
        cbxPromo.addActionListener(e -> calculateChange());

        txtCustomerCash.getDocument().addDocumentListener(new DocumentListener() {
            public void insertUpdate(DocumentEvent e) { calculateChange(); }
            public void removeUpdate(DocumentEvent e) { calculateChange(); }
            public void changedUpdate(DocumentEvent e) { calculateChange(); }
        });

        btnPayAndPrint.addActionListener(e -> processPayment());

        // [MERGE]: Gọi calculateChange() ngay sau khi dựng UI để khởi tạo tiền chính xác
        calculateChange();
    }

    private void calculateChange() {
        try {
            // [MERGE]: Đa hình OOP - Lấy thẳng DiscountStrategy đã chọn, không dùng switch-case
            DiscountStrategy selectedStrategy = (DiscountStrategy) cbxPromo.getSelectedItem();
            if (selectedStrategy != null) {
                currentOrder.setDiscountStrategy(selectedStrategy);
            }

            finalAmount = (long) currentOrder.getTotal(); // [MERGE]
            lblFinalAmount.setText(MoneyFormat.format(finalAmount)); // [MERGE]

            String cashStr = txtCustomerCash.getText().replace(",", "").replace(".", "").trim(); // [MERGE]
            if (cashStr.isEmpty()) {
                cashValid = false; // [MERGE]
                lblChange.setText("0 ₫");
                return;
            }

            long customerCash = Long.parseLong(cashStr);
            long change = customerCash - finalAmount;

            if (change >= 0) {
                cashValid = true; // [MERGE]: Đánh dấu trạng thái hợp lệ
                lblChange.setForeground(new Color(40, 167, 69)); // [MERGE]
                lblChange.setText(MoneyFormat.format(change)); // [MERGE]
            } else {
                cashValid = false; // [MERGE]
                lblChange.setForeground(Color.RED); // [MERGE]
                lblChange.setText("Khách đưa chưa đủ (thiếu " + MoneyFormat.format(-change) + ")!"); // [MERGE]
            }
        } catch (NumberFormatException ex) {
            cashValid = false; // [MERGE]
            lblChange.setForeground(Color.RED); // [MERGE]
            lblChange.setText("Lỗi định dạng số!");
        }
    }

    private void processPayment() {
        // [MERGE]: Dùng biến trạng thái cashValid thay vì so sánh text nhãn
        String cashText = txtCustomerCash.getText().trim();
        if (cashText.isEmpty() || !cashValid) {
            JOptionPane.showMessageDialog(this,
                    "Vui lòng nhập tiền khách đưa hợp lệ (phải lớn hơn hoặc bằng thành tiền)!",
                    "Lỗi thanh toán", JOptionPane.ERROR_MESSAGE); // [MERGE]
            txtCustomerCash.requestFocus(); // [MERGE]
            return;
        }

        currentOrder.markPaid(); 

        JTextArea billArea = new JTextArea();
        billArea.setFont(new Font("Monospaced", Font.PLAIN, 12));
        StringBuilder bill = new StringBuilder();
        bill.append("========== PTIT COFFEE ==========\n");
        bill.append("Bàn: ").append(currentOrder.getTableNo()).append("\n");
        bill.append("Mã đơn: ").append(currentOrder.getId()).append("\n");
        bill.append("Thời gian: ").append(currentOrder.getFormattedCreatedAt()).append("\n"); // [MERGE]
        bill.append("Khuyến mãi: ").append(currentOrder.getDiscountStrategy().toString()).append("\n"); // [MERGE]
        bill.append("---------------------------------\n");
        for (OrderItem item : currentOrder.getItems()) {
            bill.append(String.format("%-15s x%-3d %s\n", 
                item.getMenuItem().getName() + (item.getMenuItem().getSizeLabel().isEmpty() ? "" : "(" + item.getMenuItem().getSizeLabel() + ")"), // [MERGE]
                item.getQuantity(), 
                MoneyFormat.format(item.getSubtotal()))); // [MERGE]
        }
        bill.append("---------------------------------\n");
        bill.append("Tổng tiền hàng: ").append(MoneyFormat.format(currentOrder.getSubtotal())).append("\n"); // [MERGE]
        bill.append("Giảm giá:      -").append(MoneyFormat.format(currentOrder.getDiscountAmount())).append("\n"); // [MERGE]
        bill.append("THÀNH TIỀN:     ").append(MoneyFormat.format(currentOrder.getTotal())).append("\n"); // [MERGE]
        bill.append("Tiền khách đưa: ").append(MoneyFormat.format(Long.parseLong(cashText.replace(",", "").replace(".", "")))).append("\n"); // [MERGE]
        bill.append("Tiền thừa trả:  ").append(lblChange.getText()).append("\n"); // [MERGE]
        bill.append("---------------------------------\n");
        bill.append("CẢM ƠN VÀ HẸN GẶP LẠI!\n");
        
        billArea.setText(bill.toString());

        JOptionPane.showMessageDialog(this, new JScrollPane(billArea), "In Hóa Đơn", JOptionPane.INFORMATION_MESSAGE);
        
        this.dispose(); 
    }
}
