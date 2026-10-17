package vn.edu.ptit.oop.nhom05.model;

public class Food extends MenuItem {

    private int price;

    public Food(String id, String name, int price) {
        super(id, name);

        if (price < 0) {
            throw new IllegalArgumentException(
                    "Gia mon an khong duoc am");
        }

        this.price = price;
    }

    @Override
    public int getPrice() {
        return price;
    }

    @Override
    public String getCategory() {
        return "Đồ ăn";
    }
}