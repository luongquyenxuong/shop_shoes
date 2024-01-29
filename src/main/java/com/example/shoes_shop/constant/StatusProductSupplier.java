package com.example.shoes_shop.constant;

public enum StatusProductSupplier {
    IMPORTED(1),NOT_YET_IMPORTED(0);
    private final Integer statusId;

    StatusProductSupplier(Integer statusId) {
        this.statusId = statusId;
    }

    public Integer getStatusId() {
        return statusId;
    }

    // Phương thức lấy ra enum dựa trên id
    public static StatusProductSupplier getById(int id) {
        for (StatusProductSupplier statusProductSupplier : StatusProductSupplier.values()) {
            if (statusProductSupplier.getStatusId() == id) {
                return statusProductSupplier;
            }
        }
        throw new IllegalArgumentException("Không tìm thấy enum với id: " + id);
    }

}
