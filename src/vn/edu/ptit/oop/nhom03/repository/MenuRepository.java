package vn.edu.ptit.oop.nhom03.repository;

import java.util.List;

import vn.edu.ptit.oop.nhom03.model.Category;
import vn.edu.ptit.oop.nhom03.model.MenuItem;

public interface MenuRepository {
    List<MenuItem> findAll();
    List<MenuItem> findByCategory(Category category);
}