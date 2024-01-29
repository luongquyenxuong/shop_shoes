package com.example.shoes_shop.service;

import com.example.shoes_shop.entity.Image;

import java.util.List;
import java.util.UUID;

public interface ImageService {
    void saveImage(Image image);
    void deleteImage(String[] fileName);
    List<String> getListImageOfUser(UUID userId);
}
