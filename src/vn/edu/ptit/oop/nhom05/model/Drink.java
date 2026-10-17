package vn.edu.ptit.oop.nhom05.model;

public class Drink extends MenuItem {

    public enum Size {
        S, M, L
    }

    private final int basePrice;
    private Size size;

    public Drink(String id, String name, int basePrice, Size size) {
        super(id, name);
        this.basePrice = basePrice;
        this.size = size;
    }

    public Size getSize() {
        return size;
    }

    public void setSize(Size size) {
        this.size = size;
    }

    @Override
    public int getPrice() {
        switch (size) {
            case S:
                return basePrice;
            case M:
                return basePrice + 5000;
            case L:
                return basePrice + 10000;
            default:
                return basePrice;
        }
    }

    @Override
    public String getCategory() {
        return "Nước uống";
    }

    @Override
    public String toString() {
        return getName() + " (Size " + size + ") - "
                + String.format("%,d", getPrice()) + " VND";
    }
}