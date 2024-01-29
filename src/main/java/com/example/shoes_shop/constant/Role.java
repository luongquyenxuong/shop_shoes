package com.example.shoes_shop.constant;

public enum Role {
    ADMIN(1), USER(2);
    private final int roleId;

    Role(int roleId) {
        this.roleId = roleId;
    }

    public int getRoleId() {
        return roleId;
    }

    // Phương thức lấy ra enum dựa trên id
    public static Role getById(int id) {
        for (Role role : Role.values()) {
            if (role.getRoleId() == id) {
                return role;
            }
        }
        throw new IllegalArgumentException("Không tìm thấy enum với id: " + id);
    }

    }
