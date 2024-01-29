package com.example.shoes_shop.dto;

import com.example.shoes_shop.constant.ResponseStatusConstants;
import lombok.Builder;
import lombok.Data;

import java.io.Serializable;

@Data
@Builder
public class ResponseDTO<T> implements Serializable {
    private Integer status;
    private String message;
    private T data;

    public static ResponseDTO<Void> buildEmpty() {
        return ResponseDTO.<Void>builder().status(ResponseStatusConstants.NO_CONTENT).build();
    }
}
