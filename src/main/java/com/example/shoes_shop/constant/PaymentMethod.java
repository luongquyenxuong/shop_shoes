package com.example.shoes_shop.constant;

public enum PaymentMethod {
    COD(true, "fas fa-money-bill-wave"), VNPAY(true, "vnpay-icon"), PAYPAL(true, "fab fa-paypal");
    private final boolean isActive;
    private final String iconClass;

    PaymentMethod(boolean isActive, String iconClass) {
        this.isActive = isActive;
        this.iconClass = iconClass;
    }

    public boolean getIsActive() {
        return isActive;
    }

    public String getIconClass() {
        return iconClass;
    }
}
