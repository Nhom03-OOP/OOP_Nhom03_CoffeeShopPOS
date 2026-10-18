package vn.edu.ptit.oop.nhom03.model;

public class CafeTable {
    private String name;
    private int x, y;
    private int width, height;
    private int status;

    public CafeTable(String name, int x, int y, int width, int height, int status) {
        this.name = name;
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
        this.status = status;
    }


    public boolean contains(int mouseX, int mouseY) {
        return (mouseX >= x && mouseX <= x + width && mouseY >= y && mouseY <= y + height);
    }


    public String getName() { return name; }
    public int getX() { return x; }
    public int getY() { return y; }
    public int getWidth() { return width; }
    public int getHeight() { return height; }
    public int getStatus() { return status; }

    public void setStatus(int status) { this.status = status; }
}
