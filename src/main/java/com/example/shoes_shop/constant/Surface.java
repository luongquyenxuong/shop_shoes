package com.example.shoes_shop.constant;

import java.util.Arrays;

public enum Surface {
    TEXTURED("TEXTURED"),
    VENTILATED("VENTILATED"),
    SMOOTH ("SMOOTH"),
    CLEAN ("CLEAN"),
    WATERPROOF ("WATERPROOF"),
    CLASSIC("CLASSIC"),
    DISTRESSED("DISTRESSED"),
    WORN_IN("WORN-IN"),
    SUEDE("SUEDE"),
    SHINY("SHINY"),
    SPORTY("SPORTY"),
    SUEDE_ACCENTS("SUEDE ACCENTS");
    public static String[] getValues() {
        return Arrays.stream(values()).map(Surface::getName).toArray(String[]::new);
    }
    private final String name;

    Surface(String name) {
        this.name = name;
    }
    public String getName() {
        return name;
    }
    // Phương thức lấy ra enum dựa trên id
    public static Surface getByName(String name) {
        for (Surface surface : Surface.values()) {
            if (surface.getName() == name) {
                return surface;
            }
        }
        throw new IllegalArgumentException("Không tìm thấy enum với name: " + name);
    }
}
