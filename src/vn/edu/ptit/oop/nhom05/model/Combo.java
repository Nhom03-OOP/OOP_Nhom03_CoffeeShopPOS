package vn.edu.ptit.oop.nhom05.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Combo extends MenuItem {

    private final List<MenuItem> items;

    public Combo(String id, String name) {
        super(id, name);
        this.items = new ArrayList<>();
    }

    public void addItem(MenuItem item) {
        if (item == null) {
            throw new IllegalArgumentException(
                    "Mon trong combo khong duoc null");
        }

        if (item == this) {
            throw new IllegalArgumentException(
                    "Combo khong the chua chinh no");
        }

        items.add(item);
    }

    public boolean removeItem(MenuItem item) {
        return items.remove(item);
    }

    public List<MenuItem> getItems() {
        return Collections.unmodifiableList(items);
    }

    @Override
    public int getPrice() {
        int total = 0;

        for (MenuItem item : items) {
            total += item.getPrice();
        }

        return total;
    }

    @Override
    public String getCategory() {
        return "Combo";
    }
}