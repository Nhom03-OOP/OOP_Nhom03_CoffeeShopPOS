package vn.edu.ptit.oop.nhom03.exception;

/** Lỗi nghiệp vụ của đơn hàng. Unchecked: UI chỉ cần try/catch ở chỗ muốn hiện thông báo. */
public class InvalidOrderException extends RuntimeException {
    public InvalidOrderException(String message) {
        super(message);
    }
}