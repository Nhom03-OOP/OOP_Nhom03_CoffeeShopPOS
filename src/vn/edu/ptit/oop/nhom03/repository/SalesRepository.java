package vn.edu.ptit.oop.nhom03.repository;

import java.time.LocalDate;
import java.util.List;

import vn.edu.ptit.oop.nhom03.model.Order;

/** Lưu các đơn đã thanh toán. ReportWindow lấy dữ liệu từ đây. */
public interface SalesRepository {
    void save(Order order);
    List<Order> findAll();
    List<Order> findByDate(LocalDate date);
}