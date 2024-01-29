package com.example.shoes_shop.constant;

import java.util.Arrays;

public enum ShoeSoles {
    ANTI_SLIP_RUBBER("ANTI SLIP RUBBER"),FLAT_SOLE("FLAT SOLE"),SOLES_DONATED("SOLES DONATED"),EVA_BASE("EVA BASE"),SOLE_PHYLON("SOLE PHYLON");
    private final String name;

    ShoeSoles(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }
    // Phương thức lấy ra enum dựa trên id
    public static ShoeSoles getByName(String name) {
        for (ShoeSoles shoeSoles : ShoeSoles.values()) {
            if (shoeSoles.getName() == name) {
                return shoeSoles;
            }
        }
        throw new IllegalArgumentException("Không tìm thấy enum với name: " + name);
    }
    public static String[] getValues() {
        return Arrays.stream(values()).map(ShoeSoles::getName).toArray(String[]::new);
    }
}
