package com.example.shoes_shop.util;

import java.text.NumberFormat;
import java.util.Locale;

public class CurrencyFormatter {
    private CurrencyFormatter() {

    }

    public static String formatCurrency(double amount) {
        // Định dạng số tiền theo tiêu chuẩn của Việt Nam
        NumberFormat currencyFormat = NumberFormat.getCurrencyInstance(new Locale("vi", "VN"));

        // Chuyển đổi double thành chuỗi biểu diễn tiền tệ VNĐ
        return currencyFormat.format(amount);
    }

}
