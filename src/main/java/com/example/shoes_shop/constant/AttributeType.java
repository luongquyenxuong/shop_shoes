package com.example.shoes_shop.constant;

public enum AttributeType {
    COLOR(Color.getValues()),
    STYLES(Styles.getValues()),
    MATERIAL(Material.getValues()),
    SURFACE(Surface.getValues()),
//    FEEL(Shoe_Feel.getValues()),
//    FEATURE(Shoe_Feature.getValues()),
    CLOSURE(ClosureType.getValues());

    private final String[] values;

    AttributeType(String[] values) {
        this.values = values;
    }

    public String[] getValues() {
        return values;
    }
    // Phương thức để kiểm tra xem một chuỗi có trùng khớp với enum không
    public static boolean contains(String test) {
        for (AttributeType attributeType : AttributeType.values()) {
            if (!attributeType.name().equals(test)) {
                return true;
            }
        }
        return false;
    }
}
