package vn.edu.ptit.oop.nhom05.repository;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import vn.edu.ptit.oop.nhom05.model.Order;
import vn.edu.ptit.oop.nhom05.model.OrderStatus;

/**
 * Lưu các đơn đã thanh toán thành công (trong bộ nhớ).
 * ReportWindow của bạn Ánh sẽ lấy dữ liệu từ đây để hiển thị bảng và vẽ biểu đồ.
 */
public class SalesRepository {

    private final List<Order> paidOrders = new ArrayList<>();

    /** Chỉ nhận đơn đã thanh toán, tránh lưu nhầm đơn đang gọi dở. */
    public void save(Order order) {
        if (order == null) {
            throw new IllegalArgumentException("Đơn hàng không được để trống");
        }
        if (order.getStatus() != OrderStatus.PAID) {
            throw new IllegalArgumentException("Chỉ lưu đơn đã thanh toán");
        }
        paidOrders.add(order);
    }

    /** Toàn bộ đơn đã bán (bản chỉ-đọc). */
    public List<Order> findAll() {
        return Collections.unmodifiableList(paidOrders);
    }

    /** Các đơn bán trong một ngày cụ thể. */
    public List<Order> findByDate(LocalDate date) {
        List<Order> result = new ArrayList<>();
        for (Order o : paidOrders) {
            if (o.getCreatedAt().toLocalDate().equals(date)) {
                result.add(o);
            }
        }
        return result;
    }

    /** Tổng doanh thu của tất cả đơn đã lưu. */
    public double getTotalRevenue() {
        double sum = 0;
        for (Order o : paidOrders) {
            sum += o.getTotal();
        }
        return sum;
    }

    public int count() {
        return paidOrders.size();
    }
}