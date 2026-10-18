package vn.edu.ptit.oop.nhom05.ui;

import vn.edu.ptit.oop.nhom05.model.Order;
import vn.edu.ptit.oop.nhom05.model.OrderItem;
import vn.edu.ptit.oop.nhom05.service.*;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import java.awt.*;
import java.text.NumberFormat;
import java.util.Locale;

public class CheckoutDialog extends JDialog {
    private JComboBox<String> cbxPromo;
    private JTextField txtCustomerCash;
    private JLabel lblTotalAmount, lblFinalAmount, lblChange;
    private Order currentOrder;

    public CheckoutDialog(JFrame parent, Order order) {
        super(parent, "Thanh Toán Đơn Hàng - Bàn " + order.getTableNo(), true);
        this.currentOrder = order;

        setSize(400, 300);
        setLocationRelativeTo(parent);
        setLayout(new BorderLayout(10, 10));

        // --- PANEL THÔNG TIN THANH TOÁN ---
        JPanel infoPanel = new JPanel(new GridLayout(5, 2, 10, 15));
        infoPanel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        infoPanel.add(new JLabel("Tổng tiền hàng:"));
        lblTotalAmount = new JLabel(formatVND((long) order.getSubtotal()));
        infoPanel.add(lblTotalAmount);

        infoPanel.add(new JLabel("Áp dụng KM:"));
        cbxPromo = new JComboBox<>(new String[]{
                "Không giảm giá", "Giảm 10%", "Khách thành viên", "Giờ vàng (Happy Hour)"
        });
        infoPanel.add(cbxPromo);

        infoPanel.add(new JLabel("Thành tiền:"));
        lblFinalAmount = new JLabel(formatVND((long) order.getSubtotal()));
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
    }

    private void calculateChange() {
        try {
            int selectedPromo = cbxPromo.getSelectedIndex();
            switch (selectedPromo) {
                case 0: currentOrder.setDiscountStrategy(new NoDiscount()); break;
                case 1: currentOrder.setDiscountStrategy(new PercentDiscount(0.1)); break;
                case 2: currentOrder.setDiscountStrategy(new MemberDiscount()); break; // Chỗ này gọi thẳng là ăn luôn vì Quỳnh đã thêm constructor mặc định
                case 3: currentOrder.setDiscountStrategy(new HappyHourDiscount()); break;
            }

            long finalAmount = (long) currentOrder.getTotal();
            lblFinalAmount.setText(formatVND(finalAmount));

            String cashStr = txtCustomerCash.getText().replace(",", "").replace(".", "");
            if (cashStr.isEmpty()) {
                lblChange.setText("0 ₫");
                return;
            }

            long customerCash = Long.parseLong(cashStr);
            long change = customerCash - finalAmount;

            if (change >= 0) {
                lblChange.setText(formatVND(change));
            } else {
                lblChange.setText("Khách đưa chưa đủ!");
            }
        } catch (NumberFormatException ex) {
            lblChange.setText("Lỗi định dạng số!");
        }
    }

    private void processPayment() {
        if (lblChange.getText().equals("Khách đưa chưa đủ!") || lblChange.getText().equals("Lỗi định dạng số!")) {
            JOptionPane.showMessageDialog(this, "Vui lòng kiểm tra lại tiền khách đưa!", "Lỗi", JOptionPane.ERROR_MESSAGE);
            return;
        }

        currentOrder.markPaid(); 

        JTextArea billArea = new JTextArea();
        billArea.setFont(new Font("Monospaced", Font.PLAIN, 12));
        StringBuilder bill = new StringBuilder();
        bill.append("========== PTIT COFFEE ==========\n");
        bill.append("Bàn: ").append(currentOrder.getTableNo()).append("\n");
        bill.append("Mã đơn: ").append(currentOrder.getId()).append("\n");
        bill.append("---------------------------------\n");
        for (OrderItem item : currentOrder.getItems()) {
            bill.append(String.format("%-15s x%-3d %s\n", 
                item.getMenuItem().getName(), 
                item.getQuantity(), 
                formatVND((long)item.getSubtotal())));
        }
        bill.append("---------------------------------\n");
        bill.append("Tổng tiền hàng: ").append(formatVND((long)currentOrder.getSubtotal())).append("\n");
        bill.append("Giảm giá:      -").append(formatVND((long)currentOrder.getDiscountAmount())).append("\n");
        bill.append("THÀNH TIỀN:     ").append(formatVND((long)currentOrder.getTotal())).append("\n");
        bill.append("---------------------------------\n");
        bill.append("CẢM ƠN VÀ HẸN GẶP LẠI!\n");
        
        billArea.setText(bill.toString());

        JOptionPane.showMessageDialog(this, new JScrollPane(billArea), "In Hóa Đơn", JOptionPane.INFORMATION_MESSAGE);
        
        this.dispose(); 
    }

    private String formatVND(long amount) {
        return NumberFormat.getCurrencyInstance(new Locale("vi", "VN")).format(amount);
    }
}