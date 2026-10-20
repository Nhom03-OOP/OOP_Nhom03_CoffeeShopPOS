
package vn.edu.ptit.oop.nhom03.model;

public enum DrinkSize {
    S("S", "S", "Size nhỏ"),
    M("M", "M", "Size vừa"),
    L("L", "L", "Size lớn");

    private final String sizeLabel;
    private final String code;
    private final String description;

    DrinkSize(String sizeLabel, String code, String description) {
        this.sizeLabel = sizeLabel;
        this.code = code;
        this.description = description;
    }

    public String getSizeLabel() {
        return sizeLabel;
    }

    public String getCode() {
        return code;
    }

    public String getDescription() {
        return description;
    }
}
