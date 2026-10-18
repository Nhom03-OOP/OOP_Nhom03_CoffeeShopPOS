package vn.edu.ptit.oop.nhom03.repository;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import vn.edu.ptit.oop.nhom03.exception.InvalidOrderException;
import vn.edu.ptit.oop.nhom03.model.Order;
import vn.edu.ptit.oop.nhom03.model.OrderStatus;

public class InMemorySalesRepository implements SalesRepository {

    private final List<Order> paidOrders = new ArrayList<>();

    /** Chỉ nhận đơn đã thanh toán. */
    @Override
    public void save(Order order) {
        if (order == null) {
            throw new InvalidOrderException("Đơn hàng không được để trống");
        }
        if (order.getStatus() != OrderStatus.PAID) {
            throw new InvalidOrderException("Chỉ lưu đơn đã thanh toán");
        }
        paidOrders.add(order);
    }

    @Override
    public List<Order> findAll() {
        return Collections.unmodifiableList(paidOrders);
    }

    @Override
    public List<Order> findByDate(LocalDate date) {
        List<Order> result = new ArrayList<>();
        for (Order o : paidOrders) {
            if (o.getCreatedAt().toLocalDate().equals(date)) result.add(o);
        }
        return result;
    }

    // Tiện ích thêm (không nằm trong interface)
    public double getTotalRevenue() {
        double sum = 0;
        for (Order o : paidOrders) sum += o.getTotal();
        return sum;
    }

    public int count() { return paidOrders.size(); }
}