package com.example.shoes_shop.constant;

public class ResponseStatusConstants {
    private ResponseStatusConstants() {
    }

    // Các constant status cho phản hồi thành công
    public static final int SUCCESS = 200;
    public static final int CREATED = 201;
    public static final int ACCEPTED = 202;
    public static final int NO_CONTENT = 204;

    // Các constant status cho phản hồi lỗi
    public static final int BAD_REQUEST = 400;
    public static final int UNAUTHORIZED = 401;
    public static final int FORBIDDEN = 403;
    public static final int NOT_FOUND = 404;
    public static final int CONFLICT = 409;
    public static final int INTERNAL_SERVER_ERROR = 500;

}
