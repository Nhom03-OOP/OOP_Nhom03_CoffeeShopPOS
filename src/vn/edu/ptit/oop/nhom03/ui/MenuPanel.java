package vn.edu.ptit.oop.nhom05.ui;

import vn.edu.ptit.oop.nhom05.model.Category;
import vn.edu.ptit.oop.nhom05.model.MenuItem;
import vn.edu.ptit.oop.nhom05.repository.MenuRepository;
import vn.edu.ptit.oop.nhom05.util.IconLoader;
import vn.edu.ptit.oop.nhom05.util.MoneyFormat;
import vn.edu.ptit.oop.nhom05.util.Theme;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.List;

/**
 * Giao diện Thực đơn gọi món (MenuPanel) - Phân hệ Frontend do Toàn phụ trách.
 * Hiển thị các danh mục theo Tab (JTabbedPane), tự động tạo các nút món bằng vòng lặp.
 * OOP:
 * - Kế thừa: Kế thừa JPanel.
 * - Trừu tượng: Nhận dữ liệu qua interface MenuRepository.
 * - Đóng gói: Giao tiếp ra ngoài qua MenuItemSelectionListener, không giữ tham chiếu MainWindow.
 * - Đa hình: Lấy mô tả và danh mục đa hình từ MenuItem.
 */
public class MenuPanel extends JPanel {
    // Interface lồng để bắn sự kiện khi nhân viên chọn 1 món trên thực đơn
    public interface MenuItemSelectionListener {
        void onItemChosen(MenuItem item);
    }

    private final MenuRepository menuRepository;
    private MenuItemSelectionListener selectionListener;
    private JTabbedPane tabbedPane;

    public MenuPanel(MenuRepository repo) {
        if (repo == null) {
            throw new IllegalArgumentException("MenuRepository không được null");
        }
        this.menuRepository = repo;

        setLayout(new BorderLayout());
        setBorder(BorderFactory.createTitledBorder("Thực Đơn Quán Cà Phê"));
        setBackground(Theme.BACKGROUND);

        buildUI();
    }

    public void setMenuItemSelectionListener(MenuItemSelectionListener listener) {
        this.selectionListener = listener;
    }

    /**
     * Dựng lại toàn bộ giao diện menu (sử dụng khi bấm F5 hoặc View > Làm mới menu).
     */
    public void reload() {
        removeAll();
        buildUI();
        revalidate();
        repaint();
    }

    private void buildUI() {
        tabbedPane = new JTabbedPane(JTabbedPane.TOP);
        tabbedPane.setFont(Theme.FONT_BOLD);

        // Duyệt qua tất cả danh mục theo thứ tự định nghĩa của enum Category
        for (Category cat : Category.values()) {
            List<MenuItem> items = menuRepository.findByCategory(cat);
            if (items.isEmpty()) {
                continue; // Bỏ qua danh mục không có món nào
            }

            JPanel gridPanel = new JPanel(new GridLayout(0, 3, 10, 10));
            gridPanel.setBackground(Theme.BACKGROUND);
            gridPanel.setBorder(BorderFactory.createEmptyBorder(12, 12, 12, 12));

            // Icon đại diện cho từng danh mục
            String iconPath = "/resources/icons/" + cat.name().toLowerCase() + ".png";
            ImageIcon catIcon = IconLoader.load(iconPath, 18, 18);

            // Vòng lặp sinh nút bấm tự động từ danh sách MenuItem
            for (MenuItem item : items) {
                JButton itemBtn = createItemButton(item, cat);
                gridPanel.add(itemBtn);
            }

            // Bọc grid panel trong JScrollPane để cuộn mượt mà khi menu dài
            JScrollPane scrollPane = new JScrollPane(gridPanel);
            scrollPane.getVerticalScrollBar().setUnitIncrement(16);
            scrollPane.setBorder(null);

            tabbedPane.addTab(cat.getLabel(), catIcon, scrollPane);
        }

        add(tabbedPane, BorderLayout.CENTER);
    }

    private JButton createItemButton(MenuItem item, Category cat) {
        // Định dạng nhãn nút dạng HTML 2 dòng: Tên món + Giá tiền
        String htmlText = String.format(
                "<html><center><b style='font-size:12px;'>%s</b><br><span style='color:#6F4E37; font-size:11px;'>%s</span></center></html>",
                item.getName(),
                MoneyFormat.format(item.getPrice())
        );

        // Nạp icon riêng cho món hoặc dùng icon danh mục
        ImageIcon itemIcon = IconLoader.load("/resources/icons/" + cat.name().toLowerCase() + ".png", 32, 32);

        JButton btn = new JButton(htmlText, itemIcon);
        btn.setVerticalTextPosition(SwingConstants.BOTTOM);
        btn.setHorizontalTextPosition(SwingConstants.CENTER);
        btn.setToolTipText("<html>" + item.getDescription() + "</html>");
        btn.setFocusPainted(false);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn.setBackground(Color.WHITE);
        btn.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(220, 215, 210), 1, true),
                BorderFactory.createEmptyBorder(8, 6, 8, 6)
        ));

        // Hiệu ứng hover đổi màu nền
        Color defaultBg = Color.WHITE;
        Color hoverBg = new Color(245, 238, 230);
        btn.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                btn.setBackground(hoverBg);
            }

            @Override
            public void mouseExited(MouseEvent e) {
                btn.setBackground(defaultBg);
            }
        });

        // Bắn sự kiện chọn món về MainWindow
        btn.addActionListener(e -> {
            if (selectionListener != null) {
                selectionListener.onItemChosen(item);
            }
        });

        return btn;
    }
}
