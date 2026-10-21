package vn.edu.ptit.oop.nhom03.ui;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.CategoryLabelPositions;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.renderer.category.LineAndShapeRenderer;
import org.jfree.data.category.DefaultCategoryDataset;
import org.jfree.data.general.DefaultPieDataset;

import vn.edu.ptit.oop.nhom03.model.Order;
import vn.edu.ptit.oop.nhom03.model.OrderItem;
import vn.edu.ptit.oop.nhom03.repository.SalesRepository;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.*;
import java.util.List;

public class ReportWindow extends JFrame {

    private static final DateTimeFormatter DATE_FMT = DateTimeFormatter.ofPattern("dd/MM/yyyy");
    private static final DateTimeFormatter DATE_TIME_FMT = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

    private final SalesRepository salesRepo;
    private final JTabbedPane tabbedPane = new JTabbedPane();
    private final JTextField dateField = new JTextField(10);
    private final JLabel infoLabel = new JLabel();

    public ReportWindow(SalesRepository repo) {
        this.salesRepo = repo;

        setTitle("Báo cáo & Phân tích Dữ liệu - Coffee Shop POS");
        setSize(1000, 650);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);

        tabbedPane.setFont(new Font("Arial", Font.BOLD, 14));

        add(createToolbar(), BorderLayout.NORTH);
        add(tabbedPane, BorderLayout.CENTER);

        refresh();
    }

    // ---------- Thanh công cụ: lọc ngày + làm mới ----------

    private JPanel createToolbar() {
        JPanel bar = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 6));

        JButton btnToday = new JButton("Hôm nay");
        JButton btnAll = new JButton("Tất cả");
        JButton btnRefresh = new JButton("Làm mới");

        btnToday.addActionListener(e -> {
            dateField.setText(LocalDate.now().format(DATE_FMT));
            refresh();
        });
        btnAll.addActionListener(e -> {
            dateField.setText("");
            refresh();
        });
        btnRefresh.addActionListener(e -> refresh());
        dateField.addActionListener(e -> refresh()); // nhấn Enter cũng lọc

        bar.add(new JLabel("Ngày (dd/MM/yyyy):"));
        bar.add(dateField);
        bar.add(btnToday);
        bar.add(btnAll);
        bar.add(btnRefresh);
        bar.add(infoLabel);
        return bar;
    }

    /** Lấy danh sách đơn theo ô ngày: để trống = tất cả, có ngày = findByDate. Trả null nếu nhập sai. */
    private List<Order> loadOrders() {
        String text = dateField.getText().trim();
        if (text.isEmpty()) {
            return new ArrayList<>(salesRepo.findAll());
        }
        try {
            return salesRepo.findByDate(LocalDate.parse(text, DATE_FMT));
        } catch (DateTimeParseException ex) {
            JOptionPane.showMessageDialog(this,
                    "Ngày không hợp lệ. Nhập theo dạng dd/MM/yyyy, ví dụ 19/10/2026.",
                    "Lỗi nhập ngày", JOptionPane.WARNING_MESSAGE);
            return null;
        }
    }

    /** Dựng lại tất cả tab từ dữ liệu hiện tại của repository. */
    public void refresh() {
        List<Order> orders = loadOrders();
        if (orders == null) {
            return; // ngày nhập sai, giữ nguyên màn hình cũ
        }

        int selected = Math.max(0, tabbedPane.getSelectedIndex());
        tabbedPane.removeAll();
        tabbedPane.addTab("Danh sách đơn hàng", createOrderTablePanel(orders));
        tabbedPane.addTab("Top 5 Món Bán Chạy", createBarChartPanel(orders));
        tabbedPane.addTab("Doanh thu theo giờ", createLineChartPanel(orders));
        tabbedPane.addTab("Phân bổ danh mục", createPieChartPanel(orders));
        tabbedPane.setSelectedIndex(Math.min(selected, tabbedPane.getTabCount() - 1));

        infoLabel.setText("  " + orders.size() + " đơn");
    }

    // ---------- Các tab ----------

    private JPanel createOrderTablePanel(List<Order> orders) {
        JPanel panel = new JPanel(new BorderLayout());
        String[] columnNames = {"Mã Đơn", "Thời Gian", "Tổng Tiền (VNĐ)", "Trạng Thái"};
        DefaultTableModel model = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        List<Order> sorted = new ArrayList<>(orders);
    Collections.reverse(sorted);
    for (Order order : sorted) {
        model.addRow(new Object[]{
                order.getId(),
                order.getCreatedAt().format(DATE_TIME_FMT),
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

    private JPanel createBarChartPanel(List<Order> orders) {
         DefaultCategoryDataset dataset = new DefaultCategoryDataset();
    Map<String, Integer> itemCount = new HashMap<>();

    for (Order o : orders) {
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

    private JPanel createLineChartPanel(List<Order> orders) {
        DefaultCategoryDataset dataset = new DefaultCategoryDataset();
    double[] hourly = new double[24];

    for (Order o : orders) {
        hourly[o.getCreatedAt().getHour()] += o.getTotal();
    }
    for (int h = 0; h < 24; h++) {
        dataset.addValue(hourly[h], "Doanh thu", String.format("%02d:00", h));
    }

    JFreeChart lineChart = ChartFactory.createLineChart(
            "DOANH THU THEO GIỜ TRONG NGÀY", "Khung giờ", "Doanh Thu (VNĐ)",
            dataset, PlotOrientation.VERTICAL, false, true, false);

    CategoryPlot plot = lineChart.getCategoryPlot();
    LineAndShapeRenderer renderer = (LineAndShapeRenderer) plot.getRenderer();
    renderer.setDefaultShapesVisible(true);
    renderer.setDefaultStroke(new BasicStroke(2.0f));
    plot.getDomainAxis().setCategoryLabelPositions(CategoryLabelPositions.UP_45);

    return new ChartPanel(lineChart);
    }

    @SuppressWarnings({"rawtypes", "unchecked"})
    private JPanel createPieChartPanel(List<Order> orders) {
        DefaultPieDataset<String> dataset = new DefaultPieDataset();
        Map<String, Double> categoryRevenue = new HashMap<>();

        // oi.getSubtotal() là tiền hàng TRƯỚC khi áp khuyến mãi của cả đơn
        for (Order o : orders) {
            for (OrderItem oi : o.getItems()) {
                String cat = oi.getMenuItem().getCategory();
                categoryRevenue.put(cat, categoryRevenue.getOrDefault(cat, 0.0) + oi.getSubtotal());
            }
        }

        for (Map.Entry<String, Double> entry : categoryRevenue.entrySet()) {
            dataset.setValue(entry.getKey(), entry.getValue());
        }

        JFreeChart pieChart = ChartFactory.createPieChart(
                "TỈ TRỌNG DOANH THU THEO DANH MỤC (trước giảm giá)", dataset, true, true, false);

        return new ChartPanel(pieChart);
    }
}