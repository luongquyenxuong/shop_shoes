package com.example.shoes_shop.constant;

import java.util.Arrays;

public enum ClosureType {
    LACES("LACES"), ELASTIC("ELASTIC"), ZIPPER("ZIPPER"), VELCRO("VELCRO"), BUCKLE("BUCKLE"), SLIP_ON("SLIP ON");
    public static String[] getValues() {
        return Arrays.stream(values()).map(ClosureType::getName).toArray(String[]::new);
    }
    private final String name;

    ClosureType(String name) {
        this.name = name;
    }
    public String getName() {
        return name;
    }
    public static ClosureType getByName(String name) {
        for (ClosureType closureType : ClosureType.values()) {
            if (closureType.getName().equals(name)) {
                return closureType;
            }
        }
        throw new IllegalArgumentException("Không tìm thấy enum với name: " + name);
    }
}
