package vn.edu.ptit.oop.nhom03.ui;

import vn.edu.ptit.oop.nhom03.exception.InvalidOrderException;
import vn.edu.ptit.oop.nhom03.model.DrinkSize;
import vn.edu.ptit.oop.nhom03.model.MenuItem;
import vn.edu.ptit.oop.nhom03.model.OrderItem;
import vn.edu.ptit.oop.nhom03.util.MoneyFormat;
import vn.edu.ptit.oop.nhom03.util.Theme;

import javax.swing.*;
import java.awt.*;

/**
 * Hộp thoại tùy chọn món (ItemOptionDialog) - Form nhập liệu thứ nhất của đề bài.
 * Cho phép chọn Size (S/M/L), số lượng (1-99) và ghi chú (ít đường, nhiều đá...).
 * OOP:
 * - Kế thừa: Kế thừa JDialog (modal).
 * - Đa hình: Dùng item.withSize(size).getPrice() để tính giá realtime mà không dùng instanceof.
 * - Xử lý ngoại lệ: Bắt InvalidOrderException và thông báo tiếng Việt qua JOptionPane.
 */
public class
ItemOptionDialog extends JDialog {
    private final MenuItem originalItem;
    private JComboBox<DrinkSize> cbxSize;
    private JSpinner spnQuantity;
    private JTextField txtNote;
    private JLabel lblCalculatedPrice;
    private OrderItem resultItem = null;

    public ItemOptionDialog(JFrame parent, MenuItem item) {
        super(parent, "Tùy chọn món: " + item.getName(), true);
        this.originalItem = item;

        setSize(420, 360);
        setLocationRelativeTo(parent);
        setResizable(false);
        setLayout(new BorderLayout(10, 10));
        getContentPane().setBackground(Theme.BACKGROUND);

        initForm();
        updatePricePreview();
    }

    private void initForm() {
        // --- 1. TIÊU ĐỀ MÓN ---
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(Theme.PRIMARY);
        headerPanel.setBorder(BorderFactory.createEmptyBorder(12, 15, 12, 15));

        JLabel lblTitle = new JLabel(originalItem.getName());
        lblTitle.setFont(new Font("Arial", Font.BOLD, 16));
        lblTitle.setForeground(Color.WHITE);

        JLabel lblCategory = new JLabel("Danh mục: " + originalItem.getCategory());
        lblCategory.setFont(Theme.FONT_REGULAR);
        lblCategory.setForeground(new Color(230, 220, 210));

        headerPanel.add(lblTitle, BorderLayout.NORTH);
        headerPanel.add(lblCategory, BorderLayout.SOUTH);
        add(headerPanel, BorderLayout.NORTH);

        // --- 2. FORM NHẬP LIỆU ---
        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBackground(Theme.BACKGROUND);
        formPanel.setBorder(BorderFactory.createEmptyBorder(15, 20, 10, 20));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(6, 6, 6, 6);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // Dòng 1: Chọn Size
        gbc.gridx = 0; gbc.gridy = 0; gbc.weightx = 0.3;
        formPanel.add(new JLabel("Kích cỡ (Size):"), gbc);

        gbc.gridx = 1; gbc.gridy = 0; gbc.weightx = 0.7;
        cbxSize = new JComboBox<>(DrinkSize.values());
        cbxSize.setSelectedItem(DrinkSize.M); // Mặc định size M
        cbxSize.addActionListener(e -> updatePricePreview());
        formPanel.add(cbxSize, gbc);

        // Dòng 2: Số lượng
        gbc.gridx = 0; gbc.gridy = 1; gbc.weightx = 0.3;
        formPanel.add(new JLabel("Số lượng (1-99):"), gbc);

        gbc.gridx = 1; gbc.gridy = 1; gbc.weightx = 0.7;
        spnQuantity = new JSpinner(new SpinnerNumberModel(1, 1, 99, 1));
        spnQuantity.addChangeListener(e -> updatePricePreview());
        formPanel.add(spnQuantity, gbc);

        // Dòng 3: Ghi chú
        gbc.gridx = 0; gbc.gridy = 2; gbc.weightx = 0.3;
        formPanel.add(new JLabel("Ghi chú pha chế:"), gbc);

        gbc.gridx = 1; gbc.gridy = 2; gbc.weightx = 0.7;
        txtNote = new JTextField();
        txtNote.setToolTipText("Ví dụ: 50% đường, ít đá, mang đi (tối đa 100 ký tự)");
        formPanel.add(txtNote, gbc);

        // Dòng 4: Xem trước giá tiền
        gbc.gridx = 0; gbc.gridy = 3; gbc.weightx = 0.3;
        formPanel.add(new JLabel("Tạm tính:"), gbc);

        gbc.gridx = 1; gbc.gridy = 3; gbc.weightx = 0.7;
        lblCalculatedPrice = new JLabel("0 ₫");
        lblCalculatedPrice.setFont(new Font("Arial", Font.BOLD, 15));
        lblCalculatedPrice.setForeground(Theme.PRIMARY);
        formPanel.add(lblCalculatedPrice, gbc);

        add(formPanel, BorderLayout.CENTER);

        // --- 3. NÚT BẤM (BUTTONS) ---
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 10));
        buttonPanel.setBackground(Theme.BACKGROUND);

        JButton btnCancel = new JButton("Hủy bỏ");
        btnCancel.addActionListener(e -> {
            resultItem = null;
            dispose();
        });

        JButton btnAdd = new JButton("Thêm vào giỏ");
        btnAdd.setBackground(Theme.ACCENT_GREEN);
        btnAdd.setForeground(Color.WHITE);
        btnAdd.setFont(Theme.FONT_BOLD);
        btnAdd.addActionListener(e -> onConfirm());

        buttonPanel.add(btnCancel);
        buttonPanel.add(btnAdd);
        add(buttonPanel, BorderLayout.SOUTH);
    }

    private void updatePricePreview() {
        DrinkSize selectedSize = (DrinkSize) cbxSize.getSelectedItem();
        int quantity = (Integer) spnQuantity.getValue();

        // OOP: Đa hình withSize() và getPrice() không cần kiểm tra kiểu hay instanceof
        MenuItem sizedItem = originalItem.withSize(selectedSize);
        double totalPrice = sizedItem.getPrice() * quantity;
        lblCalculatedPrice.setText(MoneyFormat.format(totalPrice));
    }

    private void onConfirm() {
        try {
            DrinkSize selectedSize = (DrinkSize) cbxSize.getSelectedItem();
            int quantity = (Integer) spnQuantity.getValue();
            String note = txtNote.getText().trim();

            if (note.length() > 100) {
                JOptionPane.showMessageDialog(this,
                        "Ghi chú không được vượt quá 100 ký tự!",
                        "Lỗi nhập liệu", JOptionPane.WARNING_MESSAGE);
                return;
            }

            // OOP: Đa hình withSize tạo đối tượng món có size tương ứng
            MenuItem sizedItem = originalItem.withSize(selectedSize);

            // Tạo OrderItem và kiểm tra ngoại lệ nghiệp vụ
            resultItem = new OrderItem(sizedItem, quantity, note);
            dispose();
        } catch (InvalidOrderException ex) {
            JOptionPane.showMessageDialog(this,
                    "Lỗi dữ liệu đơn hàng: " + ex.getMessage(),
                    "Lỗi", JOptionPane.ERROR_MESSAGE);
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this,
                    "Không thể thêm món: " + ex.getMessage(),
                    "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
    }

    /**
     * Phương thức tĩnh mở form hỏi tùy chọn món.
     * @return OrderItem nếu người dùng nhấn Thêm vào giỏ, hoặc null nếu hủy.
     */
    public static OrderItem ask(JFrame parent, MenuItem item) {
        ItemOptionDialog dialog = new ItemOptionDialog(parent, item);
        dialog.setVisible(true);
        return dialog.resultItem;
    }
}
