package com.example.shoes_shop.constant;

import java.util.Arrays;

public enum Material  {
    CANVAS("CANVAS"),
    LEATHER("LEATHER"),
    PATENT_LEATHER("PATENT LEATHER"),
    SYNTHETICS("SYNTHETICS"),
    VELVET("VELVET"),
    SUEDE("SUEDE"),
    BOOST("BOOST"),
    KNIT("KNIT"),
    PRIME_GREEN("PRIME GREEN"),
    FRESH_FOAM("FRESH FOAM"),
    PIGSKIN_SUEDE("PIGSKIN SUEDE");
    public static String[] getValues() {
        return Arrays.stream(values()).map(Material::getName).toArray(String[]::new);
    }
    private final String name;
    Material(String name) {
        this.name = name;
    }
    public String getName() {
        return name;
    }
    public static Material getByName(String name) {
        for (Material material : Material.values()) {
            if (material.getName().equals(name)) {
                return material;
            }
        }
        throw new IllegalArgumentException("Không tìm thấy enum với name: " + name);
    }
}
