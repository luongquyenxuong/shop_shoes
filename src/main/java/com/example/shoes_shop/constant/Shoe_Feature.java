package com.example.shoes_shop.constant;

import java.util.Arrays;

public enum Shoe_Feature {

    SPIKE_LESS("SPIKE LESS"), WATER_RESISTANT("WATER RESISTANT"), WATERPROOF("WATERPROOF");
    public static String[] getValues() {
        return Arrays.stream(values()).map(Shoe_Feature::getName).toArray(String[]::new);
    }
    private final String name;

    Shoe_Feature(String name) {
        this.name = name;
    }
    public String getName() {
        return name;
    }
    public static Shoe_Feature getByName(String name) {
        for (Shoe_Feature shoeFeature : Shoe_Feature.values()) {
            if (shoeFeature.getName() == name) {
                return shoeFeature;
            }
        }
        throw new IllegalArgumentException("Không tìm thấy enum với name: " + name);
    }
}
