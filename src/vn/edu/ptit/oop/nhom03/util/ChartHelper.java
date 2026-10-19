package vn.edu.ptit.oop.nhom03.util;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.TreeMap;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.data.category.DefaultCategoryDataset;
import org.jfree.data.general.DefaultPieDataset;

import vn.edu.ptit.oop.nhom03.model.Order;
import vn.edu.ptit.oop.nhom03.model.OrderItem;

public final class ChartHelper {

    private static final int TOP_ITEM_LIMIT = 5;
    private static final String NO_DATA_MESSAGE = "Chưa có dữ liệu bán hàng";

    private ChartHelper() {
        // Lớp tiện ích chỉ cung cấp các phương thức static.
    }

    /**
     * Top 5 món theo tổng số lượng, gộp các size/ghi chú theo tên món.
     * Khi bằng số lượng, sắp theo tên để kết quả ổn định giữa các lần làm mới.
     *
     * @param orders danh sách đơn, không null; có thể rỗng
     * @return panel mới, có thể thêm trực tiếp vào JTabbedPane
     */
    public static ChartPanel createBarChartPanel(List<Order> orders) {
        Objects.requireNonNull(orders, "Danh sách đơn hàng không được null");
        Map<String, Integer> itemCount = new TreeMap<>();
        for (Order order : orders) {
            for (OrderItem item : order.getItems()) {
                itemCount.merge(item.getMenuItem().getName(), item.getQuantity(), Integer::sum);
            }
        }

        List<Map.Entry<String, Integer>> rankedItems = new ArrayList<>(itemCount.entrySet());
        rankedItems.sort((a, b) -> b.getValue().compareTo(a.getValue()));

        DefaultCategoryDataset dataset = new DefaultCategoryDataset();
        for (int i = 0; i < Math.min(TOP_ITEM_LIMIT, rankedItems.size()); i++) {
            Map.Entry<String, Integer> item = rankedItems.get(i);
            dataset.addValue(item.getValue(), "Số lượng", item.getKey());
        }

        JFreeChart chart = ChartFactory.createBarChart(
                "TOP 5 MÓN BÁN CHẠY NHẤT", "Tên Món", "Số Lượng Đã Bán",
                dataset, PlotOrientation.VERTICAL, false, true, false);
        NumberAxis quantityAxis = (NumberAxis) chart.getCategoryPlot().getRangeAxis();
        quantityAxis.setStandardTickUnits(NumberAxis.createIntegerTickUnits());
        return createPanel(chart);
    }

    /**
     * Doanh thu sau giảm giá, cộng theo giờ tạo đơn và sắp từ sớm đến muộn.
     * Nếu đầu vào gồm nhiều ngày thì cộng chung các đơn có cùng giờ trong ngày.
     *
     * @param orders danh sách đơn, không null; có thể rỗng
     * @return panel biểu đồ đường mới
     */
    public static ChartPanel createLineChartPanel(List<Order> orders) {
        Objects.requireNonNull(orders, "Danh sách đơn hàng không được null");
        Map<Integer, Double> hourlyRevenue = new TreeMap<>();
        for (Order order : orders) {
            hourlyRevenue.merge(order.getCreatedAt().getHour(), order.getTotal(), Double::sum);
        }

        DefaultCategoryDataset dataset = new DefaultCategoryDataset();
        for (Map.Entry<Integer, Double> entry : hourlyRevenue.entrySet()) {
            dataset.addValue(entry.getValue(), "Doanh thu", entry.getKey() + ":00");
        }

        JFreeChart chart = ChartFactory.createLineChart(
                "DOANH THU THEO GIỜ TRONG NGÀY", "Khung giờ", "Doanh Thu (VNĐ)",
                dataset, PlotOrientation.VERTICAL, false, true, false);
        return createPanel(chart);
    }

    /**
     * Doanh thu theo danh mục, dùng thành tiền đã chốt của từng dòng hàng.
     * Đây là doanh thu trước giảm giá của cả đơn, được ghi rõ trên tiêu đề.
     *
     * @param orders danh sách đơn, không null; có thể rỗng
     * @return panel biểu đồ tròn mới
     */
    public static ChartPanel createPieChartPanel(List<Order> orders) {
        Objects.requireNonNull(orders, "Danh sách đơn hàng không được null");
        Map<String, Double> categoryRevenue = new TreeMap<>();
        for (Order order : orders) {
            for (OrderItem item : order.getItems()) {
                categoryRevenue.merge(item.getMenuItem().getCategory(),
                        (double) item.getSubtotal(), Double::sum);
            }
        }

        DefaultPieDataset<String> dataset = new DefaultPieDataset<>();
        for (Map.Entry<String, Double> entry : categoryRevenue.entrySet()) {
            dataset.setValue(entry.getKey(), entry.getValue());
        }

        JFreeChart chart = ChartFactory.createPieChart(
                "TỈ TRỌNG DOANH THU THEO DANH MỤC (trước giảm giá)",
                dataset, true, true, false);
        return createPanel(chart);
    }

    private static ChartPanel createPanel(JFreeChart chart) {
        chart.getPlot().setNoDataMessage(NO_DATA_MESSAGE);
        return new ChartPanel(chart);
    }
}
