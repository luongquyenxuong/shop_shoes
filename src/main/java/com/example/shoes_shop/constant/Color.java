package com.example.shoes_shop.constant;

import java.util.Arrays;

public enum Color {
    RED,BLUE,GREEN,BROWN,TAN,WHEAT,BEIGE,WHITE,BLACK,YELLOW,GOLD,ORANGE,PINK,GREY,CAMO,MULTI,PURPLE,NAVY,WINE;
    public static String[] getValues() {
        return Arrays.stream(values()).map(Color::name).toArray(String[]::new);
    }
}
