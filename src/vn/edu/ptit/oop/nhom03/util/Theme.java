package vn.edu.ptit.oop.nhom03.util;

import java.awt.Color;
import java.awt.Font;

/** Màu sắc và font dùng chung cho giao diện quán cà phê. */
public final class Theme {
    public static final Color PRIMARY = new Color(111, 78, 55);
    public static final Color BACKGROUND = new Color(250, 247, 242);
    public static final Color ACCENT_GREEN = new Color(40, 130, 80);
    public static final Color TEXT_MUTED = new Color(110, 105, 100);
    public static final Font FONT_REGULAR = new Font("Segoe UI", Font.PLAIN, 14);
    public static final Font FONT_BOLD = new Font("Segoe UI", Font.BOLD, 14);

    private Theme() { }
}
