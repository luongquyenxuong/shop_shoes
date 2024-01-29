package com.example.shoes_shop.constant;

public enum StatusSuppliers {
    ACTIVE(true), INACTIVE(false);
    private final Boolean statusId;

    StatusSuppliers(Boolean statusId) {
        this.statusId = statusId;
    }

    public Boolean getStatusId() {
        return statusId;
    }

    // Phương thức lấy ra enum dựa trên id
    public static StatusSuppliers getById(Boolean id) {
        for (StatusSuppliers statusSuppliers : StatusSuppliers.values()) {
            if (statusSuppliers.getStatusId() == id) {
                return statusSuppliers;
            }
        }
        throw new IllegalArgumentException("Không tìm thấy enum với id: " + id);
    }

}
