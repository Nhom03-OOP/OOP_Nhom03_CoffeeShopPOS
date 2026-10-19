package vn.edu.ptit.oop.nhom03.util;

import java.text.NumberFormat;
import java.util.Currency;
import java.util.Locale;

/** Định dạng tiền Việt Nam thống nhất trên menu, giỏ hàng và hóa đơn. */
public final class MoneyFormat {
    private static final Locale VIETNAM = Locale.forLanguageTag("vi-VN");

    private MoneyFormat() { }

    public static String format(double amount) {
        NumberFormat formatter = NumberFormat.getCurrencyInstance(VIETNAM);
        formatter.setCurrency(Currency.getInstance("VND"));
        formatter.setMinimumFractionDigits(0);
        formatter.setMaximumFractionDigits(0);
        return formatter.format(amount);
    }
}
