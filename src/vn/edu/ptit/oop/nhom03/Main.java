package vn.edu.ptit.oop.nhom03;

import java.awt.Font;
import javax.swing.SwingUtilities;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.StandardChartTheme;
import vn.edu.ptit.oop.nhom03.ui.MainWindow;

public class Main {
    public static void main(String[] args) {
        // Đặt font tiếng Việt cho JFreeChart một lần duy nhất
        StandardChartTheme theme = (StandardChartTheme) StandardChartTheme.createJFreeTheme();
        theme.setExtraLargeFont(new Font("Segoe UI", Font.BOLD, 18));
        theme.setLargeFont(new Font("Segoe UI", Font.BOLD, 14));
        theme.setRegularFont(new Font("Segoe UI", Font.PLAIN, 12));
        ChartFactory.setChartTheme(theme);

        SwingUtilities.invokeLater(() -> new MainWindow().setVisible(true));
    }
}
