package vn.edu.ptit.oop.nhom03.ui;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.StandardChartTheme;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.data.category.DefaultCategoryDataset;
import org.jfree.data.general.DefaultPieDataset;

import vn.edu.ptit.oop.nhom03.model.Order;
import vn.edu.ptit.oop.nhom03.model.OrderItem;
import vn.edu.ptit.oop.nhom03.repository.SalesRepository;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.List;

public class ReportWindow extends JFrame {

    private final SalesRepository salesRepo;

    public ReportWindow(SalesRepository repo) {
        this.salesRepo = repo;
        StandardChartTheme theme = (StandardChartTheme) StandardChartTheme.createJFreeTheme();
        theme.setExtraLargeFont(new Font("Arial", Font.BOLD, 18));
        theme.setLargeFont(new Font("Arial", Font.BOLD, 14));     
        theme.setRegularFont(new Font("Arial", Font.PLAIN, 12));  
        ChartFactory.setChartTheme(theme);

        setTitle("Báo cáo & Phân tích Dữ liệu - Coffee Shop POS");
        setSize(1000, 650);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);

        JTabbedPane tabbedPane = new JTabbedPane();
        tabbedPane.setFont(new Font("Arial", Font.BOLD, 14));

        tabbedPane.addTab("Danh sách đơn hàng", createOrderTablePanel());
        tabbedPane.addTab("Top 5 Món Bán Chạy", createBarChartPanel());
        tabbedPane.addTab("Doanh thu theo giờ", createLineChartPanel());
        tabbedPane.addTab("Phân bổ danh mục", createPieChartPanel());

        add(tabbedPane, BorderLayout.CENTER);
    }

    private JPanel createOrderTablePanel() {
        JPanel panel = new JPanel(new BorderLayout());
        String[] columnNames = {"Mã Đơn", "Thời Gian", "Tổng Tiền (VNĐ)", "Trạng Thái"};
        DefaultTableModel model = new DefaultTableModel(columnNames, 0);

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

        for (Order order : salesRepo.findAll()) {
            model.addRow(new Object[]{
                    order.getId(),
                    order.getCreatedAt().format(formatter),
                    String.format("%,.0f", order.getTotal()),
                    order.getStatus().getLabel()
            });
        }

        JTable table = new JTable(model);
        table.setRowHeight(25);
        table.setFont(new Font("Arial", Font.PLAIN, 14));
        table.getTableHeader().setFont(new Font("Arial", Font.BOLD, 14));
        
        panel.add(new JScrollPane(table), BorderLayout.CENTER);
        return panel;
    }

    private JPanel createBarChartPanel() {
        DefaultCategoryDataset dataset = new DefaultCategoryDataset();
        Map<String, Integer> itemCount = new HashMap<>();

        for (Order o : salesRepo.findAll()) {
            for (OrderItem oi : o.getItems()) {
                String name = oi.getMenuItem().getName();
                itemCount.put(name, itemCount.getOrDefault(name, 0) + oi.getQuantity());
            }
        }

        List<Map.Entry<String, Integer>> list = new ArrayList<>(itemCount.entrySet());
        list.sort((a, b) -> b.getValue().compareTo(a.getValue()));

        int limit = Math.min(5, list.size());
        for (int i = 0; i < limit; i++) {
            dataset.addValue(list.get(i).getValue(), "Số lượng", list.get(i).getKey());
        }

        JFreeChart barChart = ChartFactory.createBarChart(
                "TOP 5 MÓN BÁN CHẠY NHẤT", "Tên Món", "Số Lượng Đã Bán",
                dataset, PlotOrientation.VERTICAL, false, true, false);

        return new ChartPanel(barChart);
    }

    private JPanel createLineChartPanel() {
        DefaultCategoryDataset dataset = new DefaultCategoryDataset();
        Map<Integer, Double> hourlyRevenue = new TreeMap<>();

        for (Order o : salesRepo.findAll()) {
            int hour = o.getCreatedAt().getHour();
            hourlyRevenue.put(hour, hourlyRevenue.getOrDefault(hour, 0.0) + o.getTotal());
        }

        for (Map.Entry<Integer, Double> entry : hourlyRevenue.entrySet()) {
            dataset.addValue(entry.getValue(), "Doanh thu", entry.getKey() + ":00");
        }

        JFreeChart lineChart = ChartFactory.createLineChart(
                "DOANH THU THEO GIỜ TRONG NGÀY", "Khung giờ", "Doanh Thu (VNĐ)",
                dataset, PlotOrientation.VERTICAL, false, true, false);

        return new ChartPanel(lineChart);
    }
@SuppressWarnings({"rawtypes", "unchecked"})
    private JPanel createPieChartPanel() {
        DefaultPieDataset<String> dataset = new DefaultPieDataset();
        Map<String, Double> categoryRevenue = new HashMap<>();

        for (Order o : salesRepo.findAll()) {
            for (OrderItem oi : o.getItems()) {
                String cat = oi.getMenuItem().getCategory();
                double amount = oi.getSubtotal(); 
                categoryRevenue.put(cat, categoryRevenue.getOrDefault(cat, 0.0) + amount);
            }
        }

        for (Map.Entry<String, Double> entry : categoryRevenue.entrySet()) {
            dataset.setValue(entry.getKey(), entry.getValue());
        }

        JFreeChart pieChart = ChartFactory.createPieChart(
                "TỈ TRỌNG DOANH THU THEO DANH MỤC", dataset, true, true, false);

        return new ChartPanel(pieChart);
    }
}

