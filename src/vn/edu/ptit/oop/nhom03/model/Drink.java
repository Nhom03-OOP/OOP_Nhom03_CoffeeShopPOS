
package vn.edu.ptit.oop.nhom03.model;

public class Drink extends MenuItem {

    private final int basePrice;
    private DrinkSize size;

    public Drink(String id, String name, int basePrice, DrinkSize size) {
        super(id, name);

        if (basePrice < 0) {
            throw new IllegalArgumentException(
                    "Gia do uong khong duoc am");
        }

        if (size == null) {
            throw new IllegalArgumentException(
                    "Size khong duoc null");
        }

        this.basePrice = basePrice;
        this.size = size;
    }

    public DrinkSize getSize() {
        return size;
    }

    public void setSize(DrinkSize size) {
        if (size == null) {
            throw new IllegalArgumentException(
                    "Size khong duoc null");
        }

        this.size = size;
    }

    @Override
    public boolean hasSize() {
        return true;
    }

    @Override
    public String getSizeLabel() {
        return size.getSizeLabel();
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
        return getName() + " (Size " + size.getSizeLabel() + ") - "
                + String.format("%,d", getPrice()) + " VND";
    }

    public Drink withSize(DrinkSize newSize) {
        return new Drink(this.getId(), this.getName(), this.getPrice(), newSize);
    }
}
