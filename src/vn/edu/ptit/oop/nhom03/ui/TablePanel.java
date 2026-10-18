package vn.edu.ptit.oop.nhom05.ui;

import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.List;
import javax.swing.*;
import vn.edu.ptit.oop.nhom05.model.CafeTable;

/**
 * Giao diện Sơ đồ bàn (TablePanel) - file của Phát được nâng cấp để tích hợp.
 * Trạng thái status: 0 = Trống (Xanh lá), 1 = Có khách (Đỏ), 2 = Đặt trước (Vàng).
 * ĐỀ XUẤT OOP CHO PHÁT: Nên chuyển status int sang enum TableStatus { EMPTY, OCCUPIED, RESERVED }
 * kèm mã màu tương ứng để loại bỏ switch-case ma thuật.
 */
public class TablePanel extends JPanel {
    // [MERGE]: Interface lắng nghe sự kiện chọn bàn từ MainWindow
    public interface TableSelectionListener {
        void onTableSelected(int tableNo);
    }

    private List<CafeTable> tables;
    private CafeTable selectedTable = null;
    private final List<TableSelectionListener> listeners = new ArrayList<>(); // [MERGE]

    public TablePanel() {
        // [MERGE]: Đặt kích thước ưu tiên tránh bị co 0x0 khi nhúng vào JDialog
        setPreferredSize(new Dimension(360, 260)); // [MERGE]

        tables = new ArrayList<>();
        tables.add(new CafeTable("Bàn 1", 30, 30, 80, 80, 0));
        tables.add(new CafeTable("Bàn 2", 130, 30, 80, 80, 1));
        tables.add(new CafeTable("Bàn 3", 230, 30, 80, 80, 2));
        tables.add(new CafeTable("Bàn 4", 30, 130, 80, 80, 0));

        this.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                for (int i = 0; i < tables.size(); i++) { // [MERGE]
                    CafeTable table = tables.get(i);      // [MERGE]
                    if (table.contains(e.getX(), e.getY())) {
                        selectedTable = table;
                        int tableNo = i + 1; // [MERGE]: Số bàn = chỉ số + 1

                        // [MERGE]: Tách hành vi: Click đơn = Chọn bàn; Double-click = Đổi trạng thái 0->1->2
                        if (e.getClickCount() == 2) {
                            table.setStatus((table.getStatus() + 1) % 3); // [MERGE]
                        }

                        // [MERGE]: Thông báo sự kiện chọn bàn tới các listener
                        notifyTableSelected(tableNo); // [MERGE]

                        repaint();
                        break;
                    }
                }
            }
        });
    }

    // [MERGE]: Đăng ký listener
    public void addTableSelectionListener(TableSelectionListener listener) {
        if (listener != null) {
            listeners.add(listener);
        }
    }

    // [MERGE]: Phát sự kiện chọn bàn
    private void notifyTableSelected(int tableNo) {
        for (TableSelectionListener l : listeners) {
            l.onTableSelected(tableNo);
        }
    }

    // [MERGE]: Lấy số bàn đang chọn (1-based), mặc định 1 nếu chưa chọn
    public int getSelectedTableNo() {
        if (selectedTable == null) return 1;
        for (int i = 0; i < tables.size(); i++) {
            if (tables.get(i) == selectedTable) {
                return i + 1;
            }
        }
        return 1;
    }

    // [MERGE]: Cập nhật trạng thái một bàn cụ thể (0: trống, 1: có khách, 2: đặt trước)
    public void setTableStatus(int tableNo, int status) {
        int index = tableNo - 1;
        if (index >= 0 && index < tables.size()) {
            tables.get(index).setStatus(status);
            repaint();
        }
    }

    // [MERGE]: Lấy danh sách bàn bất biến để đọc thông tin
    public List<CafeTable> getTables() {
        return tables;
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g;
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        for (CafeTable table : tables) {
            switch (table.getStatus()) {
                case 0: g2d.setColor(new Color(144, 238, 144)); break;
                case 1: g2d.setColor(new Color(255, 99, 71)); break;
                case 2: g2d.setColor(new Color(255, 215, 0)); break;   
            }
            g2d.fillRoundRect(table.getX(), table.getY(), table.getWidth(), table.getHeight(), 20, 20);

            if (table == selectedTable) {
                g2d.setColor(Color.BLUE);
                g2d.setStroke(new BasicStroke(3));
                g2d.drawRoundRect(table.getX(), table.getY(), table.getWidth(), table.getHeight(), 20, 20);
            }

            g2d.setColor(Color.BLACK);
            g2d.setFont(new Font("Arial", Font.BOLD, 14));
            FontMetrics fm = g2d.getFontMetrics();
            int textWidth = fm.stringWidth(table.getName());
            int textX = table.getX() + (table.getWidth() - textWidth) / 2;
            int textY = table.getY() + (table.getHeight() + fm.getAscent()) / 2 - 5;
            g2d.drawString(table.getName(), textX, textY);
        }
    }

    public static void main(String[] args) {
        JFrame frame = new JFrame("Test Sơ Đồ Bàn - Thành viên 9");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(400, 300);

        frame.add(new TablePanel());

        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }
}
