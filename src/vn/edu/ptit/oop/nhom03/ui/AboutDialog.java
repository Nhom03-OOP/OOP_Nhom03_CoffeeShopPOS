package vn.edu.ptit.oop.nhom05.ui;

import javax.swing.*;
import java.awt.*;

public class AboutDialog extends JDialog {

    public AboutDialog(JFrame parent) {
        super(parent, "Thông tin Đề tài", true);

        setSize(450, 400);
        setLocationRelativeTo(parent);
        setLayout(new BorderLayout());

        JPanel infoPanel = new JPanel();
        infoPanel.setLayout(new BoxLayout(infoPanel, BoxLayout.Y_AXIS));
        infoPanel.setBorder(BorderFactory.createEmptyBorder(15, 20, 15, 20));

        JLabel titleLabel = new JLabel("ĐỀ TÀI 5: QUẦY BÁN HÀNG QUÁN CÀ PHÊ");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 15));
        titleLabel.setForeground(Color.BLUE);
        titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel groupLabel = new JLabel("Thực hiện: Nhóm 10");
        groupLabel.setFont(new Font("Arial", Font.BOLD, 13));
        groupLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        String memberInfo = "<html><center>" +
                "<font color='red'><b>👑 TRƯỞNG NHÓM: Thành viên 3 - Trâm (Backend 1) 👑</b></font><br><br>" +
                "Thành viên 1: Vỹ (Quản lý Git & Phản biện)<br>" +
                "Thành viên 2: Hổ (Kiến trúc & Thuyết trình)<br>" +
                "Thành viên 4: Diễm Quỳnh (Thuật toán & Backend 2)<br>" +
                "Thành viên 5: Toàn (Menu Gọi món & Frontend 1)<br>" +
                "Thành viên 6: Đạt (Thanh toán & Frontend 2)<br>" +
                "Thành viên 7: Ánh (Phân tích dữ liệu & Biểu đồ)<br>" +
                "Thành viên 8: Bắc (Tài liệu & Demo)<br>" +
                "<b>Thành viên 9: Nguyễn Tấn Phát (Tính năng Mở rộng & Tester)</b>" +
                "</center></html>";

        JLabel memberLabel = new JLabel(memberInfo);
        memberLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        memberLabel.setFont(new Font("Arial", Font.PLAIN, 12));

        infoPanel.add(titleLabel);
        infoPanel.add(Box.createRigidArea(new Dimension(0, 10)));
        infoPanel.add(groupLabel);
        infoPanel.add(Box.createRigidArea(new Dimension(0, 15)));
        infoPanel.add(memberLabel);

        JPanel buttonPanel = new JPanel();
        JButton closeButton = new JButton("Đóng");
        closeButton.addActionListener(e -> this.dispose());
        buttonPanel.add(closeButton);

        add(infoPanel, BorderLayout.CENTER);
        add(buttonPanel, BorderLayout.SOUTH);
    }

    public static void main(String[] args) {
        JFrame dummyFrame = new JFrame();
        dummyFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        dummyFrame.setSize(600, 500);
        dummyFrame.setLocationRelativeTo(null);
        dummyFrame.setVisible(true);

        AboutDialog dialog = new AboutDialog(dummyFrame);
        dialog.setVisible(true);
    }
}
