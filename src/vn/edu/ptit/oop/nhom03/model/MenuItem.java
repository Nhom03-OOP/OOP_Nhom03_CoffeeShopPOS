package vn.edu.ptit.oop.nhom03.model;

public abstract class MenuItem{
    private final String id;
    private final String name;

    public MenuItem(String id, String name) {
        this.id = id;
        this.name = name;
    }
    public MenuItem withSize(DrinkSize newSize) {
        return this;
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public abstract int getPrice();

    public abstract String getCategory();
    
    public boolean hasSize() {
    return false;
    }
    
    public String getSizeLabel() {
        return "Không áp dụng";
    }
    
    public String getCode() {
        return id;
    }
    
    public String getDescription() {
        return name;
    }

    @Override
    public String toString() {
        return name + " - "
                + String.format("%,d", getPrice()) + " VND";
    }
}
