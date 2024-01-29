package com.example.shoes_shop.constant;

import java.util.Arrays;

public enum Shoe_Feel {

    AGILE_FLEXIBLE("AGILE FLEXIBLE"),

    SPRINGY_NEUTRAL("SPRINGY NEUTRAL"),

    CUSHIONED_SUPPORTIVE("CUSHIONED SUPPORTIVE");

    public static String[] getValues() {
        return Arrays.stream(values()).map(Shoe_Feel::getName).toArray(String[]::new);
    }
    private final String name;

    Shoe_Feel(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }
    // Phương thức lấy ra enum dựa trên id
    public static Shoe_Feel getByName(String name) {
        for (Shoe_Feel shoeFeel : Shoe_Feel.values()) {
            if (shoeFeel.getName() == name) {
                return shoeFeel;
            }
        }
        throw new IllegalArgumentException("Không tìm thấy enum với name: " + name);
    }

}
