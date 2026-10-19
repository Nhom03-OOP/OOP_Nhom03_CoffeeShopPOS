package vn.edu.ptit.oop.nhom03.ui;

import vn.edu.ptit.oop.nhom03.exception.InvalidPaymentException;
import vn.edu.ptit.oop.nhom03.model.Order;
import vn.edu.ptit.oop.nhom03.model.OrderItem;
import vn.edu.ptit.oop.nhom03.service.DiscountStrategy;
import vn.edu.ptit.oop.nhom03.service.HappyHourDiscount;
import vn.edu.ptit.oop.nhom03.service.MemberDiscount;
import vn.edu.ptit.oop.nhom03.service.NoDiscount;
import vn.edu.ptit.oop.nhom03.service.PaymentService;
import vn.edu.ptit.oop.nhom03.service.PercentDiscount;
import vn.edu.ptit.oop.nhom03.util.MoneyFormat;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import java.awt.*;
import java.util.Arrays;
import java.util.List;

/** Hop thoai thanh toan: kiem tra tien, xac nhan thanh toan va hien thi hoa don. */
public class CheckoutDialog extends JDialog {
    private final JComboBox<DiscountStrategy> cbxPromo;
    private final JTextField txtCustomerCash;
    private final JLabel lblFinalAmount;
    private final JLabel lblChange;
    private final Order currentOrder;
    private final PaymentService paymentService = new PaymentService();

    private long finalAmount;
    private boolean cashValid;

    /** Constructor tuong thich voi noi goi cu. */
    public CheckoutDialog(JFrame parent, Order order) {
        this(parent, order, Arrays.asList(
                new NoDiscount(), new PercentDiscount(10),
                new MemberDiscount(), new HappyHourDiscount()));
    }

    public CheckoutDialog(JFrame parent, Order order, List<DiscountStrategy> discountOptions) {
        super(parent, "Thanh toan don hang - Ban " + order.getTableNo(), true);
        if (order == null) throw new IllegalArgumentException("Don hang khong duoc null.");
        this.currentOrder = order;

        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setSize(460, 330);
        setLocationRelativeTo(parent);
        setLayout(new BorderLayout(10, 10));

        JPanel infoPanel = new JPanel(new GridLayout(5, 2, 10, 12));
        infoPanel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
        infoPanel.add(new JLabel("Tong tien hang:"));
        infoPanel.add(new JLabel(MoneyFormat.format(order.getSubtotal())));

        infoPanel.add(new JLabel("Ap dung khuyen mai:"));
        cbxPromo = new JComboBox<>(discountOptions == null
                ? new DiscountStrategy[0] : discountOptions.toArray(new DiscountStrategy[0]));
        if (order.getDiscountStrategy() != null) cbxPromo.setSelectedItem(order.getDiscountStrategy());
        infoPanel.add(cbxPromo);

        infoPanel.add(new JLabel("Thanh tien:"));
        lblFinalAmount = new JLabel(MoneyFormat.format(order.getTotal()));
        lblFinalAmount.setFont(new Font("Arial", Font.BOLD, 14));
        lblFinalAmount.setForeground(Color.RED);
        infoPanel.add(lblFinalAmount);

        infoPanel.add(new JLabel("Tien khach dua (VND):"));
        txtCustomerCash = new JTextField();
        txtCustomerCash.setToolTipText("Nhap so tien, vi du: 100000");
        infoPanel.add(txtCustomerCash);

        infoPanel.add(new JLabel("Tien thua:"));
        lblChange = new JLabel("Chua nhap tien");
        lblChange.setFont(new Font("Arial", Font.BOLD, 14));
        infoPanel.add(lblChange);
        add(infoPanel, BorderLayout.CENTER);

        JButton btnPayAndPrint = new JButton("Xac nhan & In hoa don");
        btnPayAndPrint.setFont(new Font("Arial", Font.BOLD, 14));
        btnPayAndPrint.setBackground(new Color(40, 167, 69));
        btnPayAndPrint.setForeground(Color.WHITE);
        JPanel buttonPanel = new JPanel();
        buttonPanel.add(btnPayAndPrint);
        add(buttonPanel, BorderLayout.SOUTH);

        cbxPromo.addActionListener(e -> calculateChange());
        txtCustomerCash.getDocument().addDocumentListener(new DocumentListener() {
            @Override public void insertUpdate(DocumentEvent e) { calculateChange(); }
            @Override public void removeUpdate(DocumentEvent e) { calculateChange(); }
            @Override public void changedUpdate(DocumentEvent e) { calculateChange(); }
        });
        btnPayAndPrint.addActionListener(e -> processPayment());
        calculateChange();
    }

    private void calculateChange() {
        DiscountStrategy selected = (DiscountStrategy) cbxPromo.getSelectedItem();
        if (selected != null) currentOrder.setDiscountStrategy(selected);
        finalAmount = Math.round(currentOrder.getTotal());
        lblFinalAmount.setText(MoneyFormat.format(finalAmount));

        String rawCash = txtCustomerCash.getText().trim();
        if (rawCash.isEmpty()) {
            cashValid = false;
            lblChange.setForeground(Color.DARK_GRAY);
            lblChange.setText("Chua nhap tien");
            return;
        }
        try {
            long change = parseCash(rawCash) - finalAmount;
            cashValid = change >= 0;
            if (cashValid) {
                lblChange.setForeground(new Color(40, 167, 69));
                lblChange.setText(MoneyFormat.format(change));
            } else {
                lblChange.setForeground(Color.RED);
                lblChange.setText("Thieu " + MoneyFormat.format(-change));
            }
        } catch (NumberFormatException ex) {
            cashValid = false;
            lblChange.setForeground(Color.RED);
            lblChange.setText("Tien khong hop le");
        }
    }

    /** Chap nhan 100000, 100.000 hoac 100,000; chi nhan so nguyen VND. */
    private long parseCash(String input) {
        String normalized = input.replace(" ", "").replace(".", "").replace(",", "");
        if (normalized.isEmpty() || !normalized.matches("\\d+")) {
            throw new NumberFormatException("Tien phai la so nguyen khong am.");
        }
        return Long.parseLong(normalized);
    }

    private void processPayment() {
        final long customerCash;
        try {
            String rawCash = txtCustomerCash.getText().trim();
            if (rawCash.isEmpty()) throw new InvalidPaymentException("Vui long nhap so tien khach dua.");
            customerCash = parseCash(rawCash);
            // PaymentService kiem tra gio hang/tien va chuyen trang thai don.
            paymentService.pay(currentOrder, customerCash);
        } catch (NumberFormatException ex) {
            showPaymentError("Tien khach dua khong hop le. Chi nhap so nguyen VND, vi du 100000.");
            txtCustomerCash.requestFocusInWindow();
            return;
        } catch (InvalidPaymentException ex) {
            showPaymentError(ex.getMessage());
            txtCustomerCash.requestFocusInWindow();
            return;
        }
        showReceipt(customerCash);
        dispose();
    }

    private void showPaymentError(String message) {
        JOptionPane.showMessageDialog(this, message, "Loi thanh toan", JOptionPane.ERROR_MESSAGE);
    }

    private void showReceipt(long customerCash) {
        JTextArea billArea = new JTextArea(18, 36);
        billArea.setEditable(false);
        billArea.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 12));
        StringBuilder bill = new StringBuilder();
        bill.append("========== PTIT COFFEE ==========\n")
            .append("Ban: ").append(currentOrder.getTableNo()).append("\n")
            .append("Ma don: ").append(currentOrder.getId()).append("\n")
            .append("Thoi gian: ").append(currentOrder.getFormattedCreatedAt()).append("\n")
            .append("Khuyen mai: ").append(currentOrder.getDiscountStrategy().getStrategyName()).append("\n")
            .append("---------------------------------\n");

        for (OrderItem item : currentOrder.getItems()) {
            String size = item.getMenuItem().getSizeLabel();
            String itemName = item.getMenuItem().getName()
                    + ((size == null || size.isEmpty()) ? "" : " (" + size + ")");
            bill.append(itemName).append(" x").append(item.getQuantity())
                .append("  ").append(MoneyFormat.format(item.getSubtotal())).append("\n");
        }

        long subtotal = Math.round(currentOrder.getSubtotal());
        long discount = Math.round(currentOrder.getDiscountAmount());
        long total = Math.round(currentOrder.getTotal());
        bill.append("---------------------------------\n")
            .append("Tam tinh:       ").append(MoneyFormat.format(subtotal)).append("\n")
            .append("Giam gia:      -").append(MoneyFormat.format(discount)).append("\n")
            .append("THANH TIEN:     ").append(MoneyFormat.format(total)).append("\n")
            .append("Tien khach dua: ").append(MoneyFormat.format(customerCash)).append("\n")
            .append("Tien thua tra:  ").append(MoneyFormat.format(customerCash - total)).append("\n")
            .append("---------------------------------\n")
            .append("CAM ON VA HEN GAP LAI!\n");
        billArea.setText(bill.toString());
        billArea.setCaretPosition(0);
        JOptionPane.showMessageDialog(this, new JScrollPane(billArea),
                "Hoa don thanh toan", JOptionPane.INFORMATION_MESSAGE);
    }
}
