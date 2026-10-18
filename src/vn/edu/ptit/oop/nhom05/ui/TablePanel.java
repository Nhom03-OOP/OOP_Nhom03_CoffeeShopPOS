package vn.edu.ptit.oop.nhom05.ui;


import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.List;
import javax.swing.*;
import vn.edu.ptit.oop.nhom05.model.CafeTable;

public class TablePanel extends JPanel {
    private List<CafeTable> tables;
    private CafeTable selectedTable = null;

    public TablePanel() {
        tables = new ArrayList<>();
        tables.add(new CafeTable("Bàn 1", 30, 30, 80, 80, 0));
        tables.add(new CafeTable("Bàn 2", 130, 30, 80, 80, 1));
        tables.add(new CafeTable("Bàn 3", 230, 30, 80, 80, 2));
        tables.add(new CafeTable("Bàn 4", 30, 130, 80, 80, 0));


        this.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                for (CafeTable table : tables) {
                    if (table.contains(e.getX(), e.getY())) {
                        selectedTable = table;
                        table.setStatus((table.getStatus() + 1) % 3);
                        repaint();
                        break;
                    }
                }
            }
        });
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
