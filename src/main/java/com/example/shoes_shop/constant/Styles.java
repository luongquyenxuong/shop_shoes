package com.example.shoes_shop.constant;

import java.util.Arrays;

public enum Styles {
    RUNNING,ATHLETIC,CAPTAIN,HERITAGE,ZEROGRAND,NANTUCKET,TOPSPIN,DAVIDSON,CANOE,SIXINCHWATERPROOF,BUSHACRE,RUGGED, FORMAL, CASUAL, CLASSIC,PREMIUM, BOOTS, SANDALS, LOAFERS;
    public static String[] getValues() {
        return Arrays.stream(values()).map(Enum::name).toArray(String[]::new);
    }
}
